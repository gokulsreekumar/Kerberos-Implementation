package messageformats;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.immutables.value.Generated;

/**
 * Immutable implementation of {@link KrbKdcReq}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableKrbKdcReq.builder()}.
 */
@Generated(from = "KrbKdcReq", generator = "Immutables")
@SuppressWarnings({"all"})
@javax.annotation.processing.Generated("org.immutables.processor.ProxyProcessor")
public final class ImmutableKrbKdcReq implements KrbKdcReq {
  private final int pvno;
  private final int msgType;
  private final PaData[] paData;
  private final KrbKdcReqBody reqBody;

  private ImmutableKrbKdcReq() {
    this.pvno = 0;
    this.msgType = 0;
    this.paData = null;
    this.reqBody = null;
  }

  private ImmutableKrbKdcReq(int pvno, int msgType, PaData[] paData, KrbKdcReqBody reqBody) {
    this.pvno = pvno;
    this.msgType = msgType;
    this.paData = paData;
    this.reqBody = reqBody;
  }

  /**
   * @return The value of the {@code pvno} attribute
   */
  @JsonProperty("pvno")
  @Override
  public int pvno() {
    return pvno;
  }

  /**
   * @return The value of the {@code msgType} attribute
   */
  @JsonProperty("msgType")
  @Override
  public int msgType() {
    return msgType;
  }

  /**
   * @return A cloned {@code paData} array
   */
  @JsonProperty("paData")
  @Override
  public PaData[] paData() {
    return paData.clone();
  }

  /**
   * @return The value of the {@code reqBody} attribute
   */
  @JsonProperty("reqBody")
  @Override
  public KrbKdcReqBody reqBody() {
    return reqBody;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link KrbKdcReq#pvno() pvno} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for pvno
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableKrbKdcReq withPvno(int value) {
    if (this.pvno == value) return this;
    return new ImmutableKrbKdcReq(value, this.msgType, this.paData, this.reqBody);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link KrbKdcReq#msgType() msgType} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for msgType
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableKrbKdcReq withMsgType(int value) {
    if (this.msgType == value) return this;
    return new ImmutableKrbKdcReq(this.pvno, value, this.paData, this.reqBody);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link KrbKdcReq#paData() paData}.
   * The array is cloned before being saved as attribute values.
   * @param elements The non-null elements for paData
   * @return A modified copy of {@code this} object
   */
  public final ImmutableKrbKdcReq withPaData(PaData... elements) {
    PaData[] newValue = elements.clone();
    return new ImmutableKrbKdcReq(this.pvno, this.msgType, newValue, this.reqBody);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link KrbKdcReq#reqBody() reqBody} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for reqBody
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableKrbKdcReq withReqBody(KrbKdcReqBody value) {
    if (this.reqBody == value) return this;
    KrbKdcReqBody newValue = Objects.requireNonNull(value, "reqBody");
    return new ImmutableKrbKdcReq(this.pvno, this.msgType, this.paData, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableKrbKdcReq} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(Object another) {
    if (this == another) return true;
    return another instanceof ImmutableKrbKdcReq
        && equalTo(0, (ImmutableKrbKdcReq) another);
  }

  private boolean equalTo(int synthetic, ImmutableKrbKdcReq another) {
    return pvno == another.pvno
        && msgType == another.msgType
        && Arrays.equals(paData, another.paData)
        && reqBody.equals(another.reqBody);
  }

  /**
   * Computes a hash code from attributes: {@code pvno}, {@code msgType}, {@code paData}, {@code reqBody}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 5381;
    h += (h << 5) + pvno;
    h += (h << 5) + msgType;
    h += (h << 5) + Arrays.hashCode(paData);
    h += (h << 5) + reqBody.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code KrbKdcReq} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return "KrbKdcReq{"
        + "pvno=" + pvno
        + ", msgType=" + msgType
        + ", paData=" + Arrays.toString(paData)
        + ", reqBody=" + reqBody
        + "}";
  }

  /**
   * Utility type used to correctly read immutable object from JSON representation.
   * @deprecated Do not use this type directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Generated(from = "KrbKdcReq", generator = "Immutables")
  @Deprecated
  @JsonDeserialize
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
  static final class Json implements KrbKdcReq {
    int pvno;
    boolean pvnoIsSet;
    int msgType;
    boolean msgTypeIsSet;
    PaData[] paData;
    KrbKdcReqBody reqBody;
    @JsonProperty("pvno")
    public void setPvno(int pvno) {
      this.pvno = pvno;
      this.pvnoIsSet = true;
    }
    @JsonProperty("msgType")
    public void setMsgType(int msgType) {
      this.msgType = msgType;
      this.msgTypeIsSet = true;
    }
    @JsonProperty("paData")
    public void setPaData(PaData[] paData) {
      this.paData = paData;
    }
    @JsonProperty("reqBody")
    public void setReqBody(KrbKdcReqBody reqBody) {
      this.reqBody = reqBody;
    }
    @Override
    public int pvno() { throw new UnsupportedOperationException(); }
    @Override
    public int msgType() { throw new UnsupportedOperationException(); }
    @Override
    public PaData[] paData() { throw new UnsupportedOperationException(); }
    @Override
    public KrbKdcReqBody reqBody() { throw new UnsupportedOperationException(); }
  }

  /**
   * @param json A JSON-bindable data structure
   * @return An immutable value type
   * @deprecated Do not use this method directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  static ImmutableKrbKdcReq fromJson(Json json) {
    ImmutableKrbKdcReq.Builder builder = ImmutableKrbKdcReq.builder();
    if (json.pvnoIsSet) {
      builder.pvno(json.pvno);
    }
    if (json.msgTypeIsSet) {
      builder.msgType(json.msgType);
    }
    if (json.paData != null) {
      builder.paData(json.paData);
    }
    if (json.reqBody != null) {
      builder.reqBody(json.reqBody);
    }
    return builder.build();
  }

  /**
   * Creates an immutable copy of a {@link KrbKdcReq} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable KrbKdcReq instance
   */
  public static ImmutableKrbKdcReq copyOf(KrbKdcReq instance) {
    if (instance instanceof ImmutableKrbKdcReq) {
      return (ImmutableKrbKdcReq) instance;
    }
    return ImmutableKrbKdcReq.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableKrbKdcReq ImmutableKrbKdcReq}.
   * <pre>
   * ImmutableKrbKdcReq.builder()
   *    .pvno(int) // required {@link KrbKdcReq#pvno() pvno}
   *    .msgType(int) // required {@link KrbKdcReq#msgType() msgType}
   *    .paData(messageformats.PaData) // required {@link KrbKdcReq#paData() paData}
   *    .reqBody(messageformats.KrbKdcReqBody) // required {@link KrbKdcReq#reqBody() reqBody}
   *    .build();
   * </pre>
   * @return A new ImmutableKrbKdcReq builder
   */
  public static ImmutableKrbKdcReq.Builder builder() {
    return new ImmutableKrbKdcReq.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableKrbKdcReq ImmutableKrbKdcReq}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @Generated(from = "KrbKdcReq", generator = "Immutables")
  public static final class Builder {
    private static final long INIT_BIT_PVNO = 0x1L;
    private static final long INIT_BIT_MSG_TYPE = 0x2L;
    private static final long INIT_BIT_PA_DATA = 0x4L;
    private static final long INIT_BIT_REQ_BODY = 0x8L;
    private long initBits = 0xfL;

    private int pvno;
    private int msgType;
    private PaData[] paData;
    private KrbKdcReqBody reqBody;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code KrbKdcReq} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(KrbKdcReq instance) {
      Objects.requireNonNull(instance, "instance");
      pvno(instance.pvno());
      msgType(instance.msgType());
      paData(instance.paData());
      reqBody(instance.reqBody());
      return this;
    }

    /**
     * Initializes the value for the {@link KrbKdcReq#pvno() pvno} attribute.
     * @param pvno The value for pvno 
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("pvno")
    public final Builder pvno(int pvno) {
      this.pvno = pvno;
      initBits &= ~INIT_BIT_PVNO;
      return this;
    }

    /**
     * Initializes the value for the {@link KrbKdcReq#msgType() msgType} attribute.
     * @param msgType The value for msgType 
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("msgType")
    public final Builder msgType(int msgType) {
      this.msgType = msgType;
      initBits &= ~INIT_BIT_MSG_TYPE;
      return this;
    }

    /**
     * Initializes the value for the {@link KrbKdcReq#paData() paData} attribute.
     * @param paData The elements for paData
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("paData")
    public final Builder paData(PaData... paData) {
      this.paData = paData.clone();
      initBits &= ~INIT_BIT_PA_DATA;
      return this;
    }

    /**
     * Initializes the value for the {@link KrbKdcReq#reqBody() reqBody} attribute.
     * @param reqBody The value for reqBody 
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("reqBody")
    public final Builder reqBody(KrbKdcReqBody reqBody) {
      this.reqBody = Objects.requireNonNull(reqBody, "reqBody");
      initBits &= ~INIT_BIT_REQ_BODY;
      return this;
    }

    /**
     * Builds a new {@link ImmutableKrbKdcReq ImmutableKrbKdcReq}.
     * @return An immutable instance of KrbKdcReq
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableKrbKdcReq build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableKrbKdcReq(pvno, msgType, paData, reqBody);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = new ArrayList<>();
      if ((initBits & INIT_BIT_PVNO) != 0) attributes.add("pvno");
      if ((initBits & INIT_BIT_MSG_TYPE) != 0) attributes.add("msgType");
      if ((initBits & INIT_BIT_PA_DATA) != 0) attributes.add("paData");
      if ((initBits & INIT_BIT_REQ_BODY) != 0) attributes.add("reqBody");
      return "Cannot build KrbKdcReq, some of required attributes are not set " + attributes;
    }
  }
}
