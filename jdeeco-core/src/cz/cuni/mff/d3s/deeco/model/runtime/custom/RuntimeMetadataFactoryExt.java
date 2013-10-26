/**
 * 
 */
package cz.cuni.mff.d3s.deeco.model.runtime.custom;

import cz.cuni.mff.d3s.deeco.model.runtime.api.Invocable;
import cz.cuni.mff.d3s.deeco.model.runtime.impl.RuntimeMetadataFactoryImpl;

/**
 * Extension of the generated RuntimeMetadataFactoryImpl which redefines create methods for class instances in the meta-model. The class
 * thus allows keeping extensions to the meta-model classes in a separate package. In order for this class to be used, the genmodel must have 
 * dynamic templates enabled and a template has to be provided (located in directory /model/templates) that redefines the method {@link RuntimeMetadataFactoryImpl#init()} to instantiate this
 * class as the factory instead of the generated one. 
 * 
 * @author Tomas Bures <bures@d3s.mff.cuni.cz>
 *
 */
public class RuntimeMetadataFactoryExt extends RuntimeMetadataFactoryImpl {

	/**
	 * 
	 */
	public RuntimeMetadataFactoryExt() {
		super();
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.model.runtime.impl.RuntimeMetadataFactoryImpl#createInvocable()
	 */
	@Override
	public Invocable createInvocable() {
		return new InvocableExt();
	}
	
	

}
