import sys
import pyopencl as cl
import pyopencl.tools
import pyopencl.array
import numpy as np
from openalea.plantgl.all import *
import structfill

#Convert a float to a int (between 0 and 255)
def f2i(f):
    i = round(f * 255)
    if i < 0:
        i = 0
    if i > 255:
        i = 255
    return i

#Compute the average color (an int)
def getAverageColor(color):
    return (f2i(color[0]) << 16) + (f2i(color[1]) << 8) + f2i(color[2]) + (255 << 24)

#Compute the 3 floats power values of a pointLight with 1 float power value and RGB color.
def getPower3f(power, color):
    power = np.float32(power)
    assert type(color) == openalea.plantgl.math._pglmath.Vector3, "Color must be a PlantGL Vector3." 
    return color * (3 * power / (color[0] + color[1] + color[2]))

class LightSerializer():
    def __init__(self, bins) -> None:

        #Structures
        self.firstInfos = [("type", np.int32), ("samples", np.int32)]

        self.matrix = [("WtOMatrix", np.float32, 12)]

        self.invmatrix = [("OtWMatrix", np.float32, 12)]
        
        self.power = [("power", np.float32, 3)]

        self.spectralCdf = [("spectralCdf", np.float32, bins)]
        
        self.spectralDistribution = [("rgb", np.float32, 3), ("spectralPower", np.float32, bins)]

        self.light = self.firstInfos + self.matrix + self.invmatrix + self.power + self.spectralCdf

        self.spectralLight = self.light + self.spectralDistribution

        #Attributes
        self.lightList = []

    #Add a point light to the list
    def addPointLight(self, samples, color, power, spectralCdF):
        self.lightList.append((0, {"samples": samples, "color": color, "power": power, "spectralCdF": spectralCdF}))

    #Add a spectral light to the list
    def addSpectralLight(self, samples, color, power, spectralCdF, rgb, distribution):
        self.lightList.append((6, {"samples": samples, "color": color, "power": power, "spectralCdF": spectralCdF, "rgb": rgb, "distribution": distribution}))

    #Set the basic infos of a light source
    def setLightBase(self, lightType, samples, matrix, color, power, spectralCdf, buffer):
        #Type check
        lighType = np.int32(lightType)
        samples = np.int32(samples)
        assert len(spectralCdf) == len(buffer["spectralCdF"]), 'Lenght of Spectral CDF must equals to the Wavelenghts bins.'

        #First infos
        buffer["type"].fill(lightType)
        buffer["samples"].fill(samples)
        

        #World to Object Matrix
        structfill.fillMatrix34(buffer, "WtOMatrix", matrix)

        #Object to world matrix
        invMatrix = matrix.inverse()
        structfill.fillMatrix34(buffer, "OtWMatrix", invMatrix)

        #Power
        power3f = getPower3f(power, color)
        structfill.fillVec3(buffer, "power", power3f)

        #SpectralCdF
        buffer["spectralCdf"] = spectralCdf

    #Serialize a point light source
    def serializePointLight(self, samples, color, power, spectralCdf, bins):
        
        pointLight = np.array(1, dtype=self.light)

        #Set base (point light only have a base structure)
        self.setLightBase(0, samples, Matrix4((1, 0, 0, 0 , 0, 1, 0, 0 , 0, 0, 1, 0 , 0, 0, 0, 1)), color, power, spectralCdf, pointLight)

        lightInBytes = pointLight.tobytes()

        return lightInBytes, len(lightInBytes), max(power, 0)

    #Serialize a spectral light source
    def serializeSpectralLight(self, samples, color, power, spectralCdF, rgb, distribution):
       
        spectralLight = np.array(1, dtype=self.spectralLight)        
        
        #Type check
        assert len(distribution) == len(spectralLight["spectralPower"])

        #Set base
        self.setLightBase(6, samples, Matrix4((1, 0, 0, 0 , 0, 1, 0, 0 , 0, 0, 1, 0 , 0, 0, 0, 1)), color, power, spectralCdF, spectralLight)
        
        #RGB
        structfill.fillVec3(spectralLight, "rgb", rgb)

        #Spectral Disrtibution
        spectralLight["spectralPower"] = distribution

        lightInBytes = spectralLight.tobytes()

        #TotalPower
        avercolor = getAverageColor(color)
        colorPower = (((avercolor >> 0) & 0xFF) + ((avercolor >> 8) & 0xFF) + ((avercolor >> 16) & 0xFF)) / (256.0);
        totalPower = (power / colorPower) * power

        return lightInBytes, len(spectralLight), max(totalPower, 0)

    # Serialize all lights in the list
    def serialize():
        if(self.lightList[0][0] == 0)
            serializePointLight(self.lightList[0][1]["samples"], self.lightList[0][1]["color"], self.lightList[0][1]["power"], self.lightList[0][1]["spectralCdF"])
        if(self.lightList[0][0] == 6)
            serializePointLight(self.lightList[0][1]["samples"], self.lightList[0][1]["color"], self.lightList[0][1]["power"], self.lightList[0][1]["spectralCdF"], self.lightList[0][1]["rgb"], self.lightList[0][1]["distribution"])
        for light in self.lightList[1:]:
            if(light[0] == 0)
                serializePointLight(light[1]["samples"], light[1]["color"], light[1]["power"], light[1]["spectralCdF"])
            if(light[0] == 6)
                serializePointLight(light[1]["samples"], light[1]["color"], light[1]["power"], light[1]["spectralCdF"], light[1]["rgb"], light[1]["distribution"])