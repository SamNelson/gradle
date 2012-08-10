/*
 * Copyright 2012 the original author or authors.
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

package org.gradle.api.plugins.migration.model.compare;

import org.gradle.api.plugins.migration.model.association.BuildOutcomeAssociation;
import org.gradle.api.plugins.migration.model.outcome.BuildOutcome;

/**
 * Builder for build comparison specifications.
 */
public interface BuildComparisonSpecBuilder {

    <A extends BuildOutcome, F extends A, T extends A> BuildOutcomeAssociation<A> associate(F from, T to, Class<A> type);

    <F extends BuildOutcome> void addUnassociatedFrom(F from);

    <T extends BuildOutcome> void addUnassociatedTo(T to);

    BuildComparisonSpec build();

}