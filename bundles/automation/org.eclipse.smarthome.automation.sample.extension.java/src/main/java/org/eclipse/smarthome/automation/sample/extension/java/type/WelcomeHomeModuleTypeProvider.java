/**
 * Copyright (c) 1997, 2015 by ProSyst Software GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.automation.sample.extension.java.type;

import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import org.eclipse.smarthome.automation.Rule;
import org.eclipse.smarthome.automation.template.RuleTemplate;
import org.eclipse.smarthome.automation.type.ModuleType;
import org.eclipse.smarthome.automation.type.ModuleTypeProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * The purpose of this class is to illustrate how to provide Module Types and how to use them for creation of the
 * {@link RuleTemplate}s or directly the {@link Rule}s.
 *
 * @author Ana Dimova - Initial Contribution
 *
 */
public class WelcomeHomeModuleTypeProvider implements ModuleTypeProvider {

    private Map<String, ModuleType> providedModuleTypes;
    @SuppressWarnings("rawtypes")
    private ServiceRegistration providerReg;

    public WelcomeHomeModuleTypeProvider() {
        providedModuleTypes = new HashMap<String, ModuleType>();
        providedModuleTypes.put(WelcomeHomeActionType.UID, WelcomeHomeActionType.initialize());
        providedModuleTypes.put(StateConditionType.UID, StateConditionType.initialize());
        providedModuleTypes.put(TemperatureConditionType.UID, TemperatureConditionType.initialize());
        providedModuleTypes.put(AirConditionerTriggerType.UID, AirConditionerTriggerType.initialize());
        providedModuleTypes.put(LightsTriggerType.UID, LightsTriggerType.initialize());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ModuleType> T getModuleType(String UID, Locale locale) {
        return (T) providedModuleTypes.get(UID);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ModuleType> Collection<T> getModuleTypes(Locale locale) {
        return (Collection<T>) providedModuleTypes.values();
    }

    /**
     * To provide the {@link ModuleType}s should register the WelcomeHomeModuleTypeProvider as
     * {@link ModuleTypeProvider} service.
     *
     * @param bc
     *            is a bundle's execution context within the Framework.
     */
    public void register(BundleContext bc) {
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put(REG_PROPERTY_MODULE_TYPES, providedModuleTypes.keySet());
        providerReg = bc.registerService(ModuleTypeProvider.class.getName(), this, properties);
    }

    /**
     * This method unregisters the WelcomeHomeModuleTypeProvider as {@link ModuleTypeProvider}
     * service.
     */
    public void unregister() {
        providerReg.unregister();
        providerReg = null;
        providedModuleTypes = null;
    }

}
