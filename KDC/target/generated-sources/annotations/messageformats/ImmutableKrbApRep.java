package messageformats;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Objects;
import org.immutables.value.Generated;

/**
 * Immutable implementation of {@link KrbApRep}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableKrbApRep.builder()}.
 */
@Generated(from = "KrbApRep", generator = "Immutables")
@SuppressWarnings({"all"})
@javax.annotation.processing.Generated("org.immutables.processor.ProxyProcessor")
public final class ImmutableKrbApRep implements KrbApRep {

  private ImmutableKrbApRep() {}

  private ImmutableKrbApRep(ImmutableKrbApRep.Builder builder) {
  }

  /**
   * This instance is equal to all instances of {@code ImmutableKrbApRep} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(Object another) {
    if (this == another) return true;
    return another instanceof ImmutableKrbApRep
        && equalTo(0, (ImmutableKrbApRep) another);
  }

  @SuppressWarnings("MethodCanBeStatic")
  private boolean equalTo(int synthetic, ImmutableKrbApRep another) {
    return true;
  }

  /**
   * Returns a constant hash code value.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    return -1675945109;
  }

  /**
   * Prints the immutable value {@code KrbApRep}.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return "KrbApRep{}";
  }

  /**
   * Utility type used to correctly read immutable object from JSON representation.
   * @deprecated Do not use this type directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Generated(from = "KrbApRep", generator = "Immutables")
  @Deprecated
  @JsonDeserialize
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
  static final class Json implements KrbApRep {
  }

  /**
   * @param json A JSON-bindable data structure
   * @return An immutable value type
   * @deprecated Do not use this method directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  static ImmutableKrbApRep fromJson(Json json) {
    ImmutableKrbApRep.Builder builder = ImmutableKrbApRep.builder();
    return builder.build();
  }

  /**
   * Creates an immutable copy of a {@link KrbApRep} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable KrbApRep instance
   */
  public static ImmutableKrbApRep copyOf(KrbApRep instance) {
    if (instance instanceof ImmutableKrbApRep) {
      return (ImmutableKrbApRep) instance;
    }
    return ImmutableKrbApRep.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableKrbApRep ImmutableKrbApRep}.
   * <pre>
   * ImmutableKrbApRep.builder()
   *    .build();
   * </pre>
   * @return A new ImmutableKrbApRep builder
   */
  public static ImmutableKrbApRep.Builder builder() {
    return new ImmutableKrbApRep.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableKrbApRep ImmutableKrbApRep}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @Generated(from = "KrbApRep", generator = "Immutables")
  public static final class Builder {

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code KrbApRep} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(KrbApRep instance) {
      Objects.requireNonNull(instance, "instance");
      return this;
    }

    /**
     * Builds a new {@link ImmutableKrbApRep ImmutableKrbApRep}.
     * @return An immutable instance of KrbApRep
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableKrbApRep build() {
      return new ImmutableKrbApRep(this);
    }
  }
}
