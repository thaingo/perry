package gov.ca.cwds.idm.service.cognito.attribute;

import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.attribute;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import gov.ca.cwds.idm.service.cognito.attribute.AttributesBuilder;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class AttributesBuilderTest {

  private AttributesBuilder builder;

  @Before
  public void setUp() {
    builder = new AttributesBuilder();
  }

  @Test
  public void testAddAttributeWithNameValue() {
    builder
        .addAttribute(() -> "firstName", "firstValue")
        .addAttribute(() -> "secondName", "")
        .addAttribute(() -> "thirdName", "   ")
        .addAttribute(() -> "forthName", null);
    List<AttributeType> attrs = builder.build();
    assertThat(attrs, hasSize(4));

    assertThat(attrs.get(0).getName(), is("firstName"));
    assertThat(attrs.get(0).getValue(), is("firstValue"));

    assertThat(attrs.get(1).getName(), is("secondName"));
    assertThat(attrs.get(1).getValue(), is(""));

    assertThat(attrs.get(2).getName(), is("thirdName"));
    assertThat(attrs.get(2).getValue(), is(""));

    assertThat(attrs.get(3).getName(), is("forthName"));
    assertThat(attrs.get(3).getValue(), is(""));
  }

  @Test
  public void testAddAttribute() {
    builder
        .addAttribute(attribute("firstName", "firstValue"))
        .addAttribute(attribute("secondName", ""))
        .addAttribute(attribute("thirdName", "   "))
        .addAttribute(attribute("forthName", null));
    List<AttributeType> attrs = builder.build();
    assertThat(attrs, hasSize(4));

    assertThat(attrs.get(0).getName(), is("firstName"));
    assertThat(attrs.get(0).getValue(), is("firstValue"));

    assertThat(attrs.get(1).getName(), is("secondName"));
    assertThat(attrs.get(1).getValue(), is(""));

    assertThat(attrs.get(2).getName(), is("thirdName"));
    assertThat(attrs.get(2).getValue(), is(""));

    assertThat(attrs.get(3).getName(), is("forthName"));
    assertThat(attrs.get(3).getValue(), is(""));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddAttributeNullAttribute() {
    builder.addAttribute(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddAttributeNullUserAttribute() {
    builder.addAttribute(null, "value");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddAttributeNullName() {
    builder.addAttribute(() -> null, "value");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddAttributeEmptyName() {
    builder.addAttribute(() -> "", "value");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddAttributeBlankName() {
    builder.addAttribute(() -> "  ", "value");
  }

  @Test
  public void testBuildEmptyBuider() {
    assertThat(builder.build(), empty());
  }
}
