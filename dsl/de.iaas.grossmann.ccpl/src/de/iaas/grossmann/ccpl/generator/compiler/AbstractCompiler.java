package de.iaas.grossmann.ccpl.generator.compiler;

import org.eclipse.emf.ecore.resource.Resource;

public abstract class AbstractCompiler {
	
	private Resource resource;
	
	public AbstractCompiler(Resource resource) {
		this.resource = resource;
	}
	
	public abstract String compile();
	
	public Resource getResource() {
		return this.resource;
	}
}
