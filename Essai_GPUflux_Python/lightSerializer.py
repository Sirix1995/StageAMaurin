import sys
import pyopencl as cl
import pyopencl.tools
import pyopencl.array
import numpy as np
from openalea.plantgl.all import *  

class LightSerializer():
    def __init__(self, bins) -> None:

        self.firstInfos = [("type", np.int32), ("samples", np.int32)]

        self.matrix = [("mat00", np.float32), ("mat01", np.float32), ("mat02", np.float32),
                       ("mat10", np.float32), ("mat11", np.float32), ("mat12", np.float32),
                       ("mat20", np.float32), ("mat21", np.float32), ("mat22", np.float32),
                       ("mat30", np.float32), ("mat31", np.float32), ("mat32", np.float32)]

        self.invmatrix = [("imat00", np.float32), ("imat01", np.float32), ("imat02", np.float32),
                          ("imat10", np.float32), ("imat11", np.float32), ("imat12", np.float32),
                          ("imat20", np.float32), ("imat21", np.float32), ("imat22", np.float32),
                          ("imat30", np.float32), ("imat31", np.float32), ("imat32", np.float32)]
        
        self.power = [("powerX", np.float32), ("powerY", np.float32), ("powerZ", np.float32)]

        self.spectralCdf = []
        for i in range(bins):
            self.spectralCdf.append(("bin" + i,np.float32))

        self.light = self.firstInfos + self.matrix + self.invmatrix + self.power + self.spectralCdf

    def setLightBase(self, lightType, samples, power, spectralCdf, bins, buffer):
        assert type(lightType) == int, 'Light Type must be an int value.'
        assert type(samples) == int, 'Samples must be an int value.'
        assert len(power) == 3, 'Power must be a list of three values.'

        #First infos
        buffer["type"].fill(lightType)
        buffer["samples"].fill(samples)
        
        #World to object matrix
        buffer["mat00"].fill(1.0)
        buffer["mat01"].fill(0.0)
        buffer["mat02"].fill(0.0)
        buffer["mat10"].fill(0.0)
        buffer["mat11"].fill(1.0)
        buffer["mat12"].fill(0.0)
        buffer["mat20"].fill(0.0)
        buffer["mat21"].fill(0.0)
        buffer["mat22"].fill(1.0)
        buffer["mat30"].fill(0.0)
        buffer["mat31"].fill(0.0)
        buffer["mat32"].fill(0.0)

        #Object to world matrix
        WtO = Matrix4(1, 0, 0, 0 , 0, 1, 0, 0 , 0, 0, 1, 0 , 0, 0, 0, 1)
        OtW = WtO.inverse()

        buffer["imat00"].fill(OtW[0])
        buffer["imat01"].fill(OtW[1])
        buffer["imat02"].fill(OtW[2])
        buffer["imat10"].fill(OtW[3])
        buffer["imat11"].fill(OtW[4])
        buffer["imat12"].fill(OtW[5])
        buffer["imat20"].fill(OtW[6])
        buffer["imat21"].fill(OtW[7])
        buffer["imat22"].fill(OtW[8])
        buffer["imat30"].fill(OtW[9])
        buffer["imat31"].fill(OtW[10])
        buffer["imat32"].fill(OtW[11])

        #Power
        buffer[("powerX", power[0])]
        buffer[("powerY", power[1])]
        buffer[("powerZ", power[2])]

        #SpectralCdF
        for i in range(bins):
            buffer["bin" + i].fill(spectralCdf[i])

    def serializePointLight(self, samples, power, spectralCdf, bins):
        pointLight = np.array(dtype=self.light)
        self.setLightBase(0, samples, power, spectralCdf, bins, pointLight)
        return pointLight