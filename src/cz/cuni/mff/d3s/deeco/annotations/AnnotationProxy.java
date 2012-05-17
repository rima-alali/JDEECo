/*******************************************************************************
 * Copyright 2012 Charles University in Prague
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package cz.cuni.mff.d3s.deeco.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 * 
 * @author Michal Kit
 *
 */
public class AnnotationProxy implements InvocationHandler {

	private Annotation annotation;
	private Class annotationClass;

	public static Object implement(Class interfaceToImplement,
			Annotation annotation) {
		return Proxy.newProxyInstance(interfaceToImplement.getClassLoader(),
				new Class[] { interfaceToImplement }, new AnnotationProxy(
						annotation));
	}

	public AnnotationProxy(Annotation annotation) {
		this.annotation = annotation;
		this.annotationClass = annotation.getClass();
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Method realMethod = annotationClass.getMethod(method.getName(),
				method.getParameterTypes());
		return realMethod.invoke(annotation, args);
	}
}
