package jmind.pigg.binding;

import org.junit.Test;

import jmind.pigg.binding.BindingParameter;
import jmind.pigg.binding.BindingParameterInvoker;
import jmind.pigg.binding.DefaultParameterContext;
import jmind.pigg.binding.FunctionalBindingParameterInvoker;
import jmind.pigg.binding.InvocationContext;
import jmind.pigg.binding.InvocationContextFactory;
import jmind.pigg.binding.ParameterContext;
import jmind.pigg.descriptor.ParameterDescriptor;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author xieweibo
 */
public class InvocationContextFactoryTest {

  @Test
  public void testNewInvocationContext() throws Exception {
    List<Annotation> empty = Collections.emptyList();
    ParameterDescriptor p0 = ParameterDescriptor.create(0, String.class, empty, "name");
    ParameterDescriptor p1 = ParameterDescriptor.create(1, int.class, empty, "id");
    List<ParameterDescriptor> pds = Arrays.asList(p0, p1);
    ParameterContext paramCtx = DefaultParameterContext.create(pds);
    InvocationContextFactory factory = InvocationContextFactory.create(paramCtx);
    InvocationContext invCtx = factory.newInvocationContext(new Object[]{"ash", 9527});
    BindingParameterInvoker nameInvoker =
        FunctionalBindingParameterInvoker.create(String.class, BindingParameter.create("name", "", null));
    BindingParameterInvoker idInvoker =
        FunctionalBindingParameterInvoker.create(String.class, BindingParameter.create("id", "", null));
    assertThat(invCtx.getNullableBindingValue(nameInvoker), equalTo((Object) "ash"));
    assertThat(invCtx.getNullableBindingValue(idInvoker), equalTo((Object) 9527));

  }

}
