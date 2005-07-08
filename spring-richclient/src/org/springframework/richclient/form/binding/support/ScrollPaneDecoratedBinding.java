/*
 * Copyright 2002-2004 the original author or authors.
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
package org.springframework.richclient.form.binding.support;

import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.application.Application;

/**
 * A convenience class that decorates the component produced from a source
 * Binding with a JScrollPane.  Useful for placing JList (and JTextArea)
 * bindings, among others, in a scroll pane when needed.
 * 
 * @author Andy DePue
 */
public class ScrollPaneDecoratedBinding extends DecoratedControlBinding
{
  public ScrollPaneDecoratedBinding(final Binding source)
  {
    super(source, Application.services().getComponentFactory().createScrollPane(source.getControl()));
  }
  
  public ScrollPaneDecoratedBinding(final Binding source, final int vsbPolicy, final int hsbPolicy)
  {
    super(source, Application.services().getComponentFactory().createScrollPane(source.getControl(), vsbPolicy, hsbPolicy));
  }
}
