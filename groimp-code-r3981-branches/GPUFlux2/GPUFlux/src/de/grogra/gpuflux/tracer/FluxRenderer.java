package de.grogra.gpuflux.tracer;

import java.io.IOException;

import de.grogra.gpuflux.GPUFlux;
import de.grogra.imp.Renderer;
import de.grogra.ray2.Options;
import de.grogra.reflect.Type;
import de.grogra.util.EnumerationType;
import de.grogra.util.Map;


public class FluxRenderer extends Renderer implements Options {

	public static final EnumerationType TRACER = new EnumerationType (
			"ray.processor", GPUFlux.I18N, new String[] {"whittedtracer", "pathtracer", "lighttracer"},
			new Class[] {FluxWhittedTracer.class, FluxPathTracer.class, FluxLightTracer.class}, Type.CLASS);
	private Map params;

	public FluxRenderer(Map params)
	{
		this.params = params;
	}
	
	public void dispose() {
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void render() throws IOException {
		Object obj = (Object)getClassOption( "renderer/tracer" , null );
		
		if( obj instanceof FluxTracer )
		{
			FluxTracer tracer = (FluxTracer)obj;
			tracer.render(view, width, height, observers, this);
		}
	}
	
	public Object getClassOption (String key, Object def)
	{
		Object cls = get (key, null);
		if (cls instanceof Class)
		{
			try
			{
				return ((Class) cls).newInstance ();
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}
		return def;
	}
	
	public Object get (String key, Object def)
	{
		return params.get (key, def);
	}

}
