/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.internal

import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.HelperUtil

import spock.lang.Specification

class DefaultPolymorphicDomainObjectContainerDslTest extends Specification {
    def fred = new DefaultPerson(name: "Fred")
    def barney = new DefaultPerson(name: "Barney")
    def agedFred = new DefaultAgeAwarePerson(name: "Fred", age: 42)
    def agedBarney = new DefaultAgeAwarePerson(name: "Barney", age: 42)

    def project = HelperUtil.createRootProject()
    def instantiator = project.services.get(Instantiator)
    def container = instantiator.newInstance(DefaultPolymorphicDomainObjectContainer, Person, instantiator)

    def setup() {
        project.extensions.add("container", container)
    }

    interface Person extends Named {}

    static class DefaultPerson implements Person {
        String name
        String toString() { name }


        boolean equals(DefaultPerson other) {
            name == other.name
        }

        int hashCode() {
            name.hashCode()
        }
    }

    interface AgeAwarePerson extends Person {
        int getAge()
    }

    static class DefaultAgeAwarePerson extends DefaultPerson implements AgeAwarePerson {
        int age

        boolean equals(DefaultAgeAwarePerson other) {
            name == other.name && age == other.age
        }

        int hashCode() {
            name.hashCode() * 31 + age
        }
    }

    def "create elements with default type"() {
        container.registerDefaultFactory({ new DefaultPerson(name: it) } as NamedDomainObjectFactory )

        when:
        project.container {
            Fred
            Barney {}
        }

        then:
        container.size() == 2
        container.findByName("Fred") == fred
        container.findByName("Barney") == barney
        container.asDynamicObject.getProperty("Fred") == fred
        container.asDynamicObject.getProperty("Barney") == barney
    }

    def "configure elements with default type"() {

    }

    def "create elements with specified type"() {
        container.registerFactory(Person, { new DefaultPerson(name: it) } as NamedDomainObjectFactory)
        container.registerFactory(AgeAwarePerson, { new DefaultAgeAwarePerson(name: it, age: 42) } as NamedDomainObjectFactory)

        when:
        project.container {
            Fred(Person) {}
            Barney(AgeAwarePerson) {}
        }

        then:
        container.size() == 2
        container.findByName("Fred") == fred
        container.findByName("Barney") == agedBarney
        container.asDynamicObject.getProperty("Fred") == fred
        container.asDynamicObject.getProperty("Barney") == agedBarney

    }
}
