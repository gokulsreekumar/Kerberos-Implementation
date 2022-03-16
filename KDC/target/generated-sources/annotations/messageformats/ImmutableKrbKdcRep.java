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
 * Immutable implementation of {@link KrbKdcRep}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableKrbKdcRep.builder()}.
 */
@Generated(from = "KrbKdcRep", generator = "Immutables")
@SuppressWarnings({"all"})
@javax.annotation.processing.Generated("org.immutables.processor.ProxyProcessor")
public final class ImmutableKrbKdcRep implements KrbKdcRep {
  private final int pvno;
  private final int msgType;
  private final PaData[] paData;
  private final PrincipalName cname;
  private final Ticket ticket;
  private final EncryptedData encPart;

  private ImmutableKrbKdcRep() {
    this.pvno = 0;
    this.msgType = 0;
    this.paData = null;
    this.cname = null;
    this.ticket = null;
    this.encPart = null;
  }

  private ImmutableKrbKdcRep(
      int pvno,
      int msgType,
      PaData[] paData,
      PrincipalName cname,
      Ticket ticket,
      EncryptedData encPart) {
    this.pvno = pvno;
    this.msgType = msgType;
    this.paData = paData;
    this.cname = cname;
    this.ticket = ticket;
    this.encPart = encPart;
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
   * @return The value of the {@code cname} attribute
   */
  @JsonProperty("cname")
  @Override
  public PrincipalName cname() {
    return cname;
  }

  /**
   * @return The value of the {@code ticket} attribute
   */
  @JsonProperty("ticket")
  @Override
  public Ticket ticket() {
    return ticket;
  }

  /**
   * @return The value of the {@code encPart} attribute
   */
  @JsonProperty("encPart")
  @Override
  public EncryptedData encPart() {
    return encPart;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link KrbKdcRep#pvno() pvno} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for pvno
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableKrbKdcRep withPvno(int value) {
    if (this.pvno == value) return this;
    return new ImmutableKrbKdcRep(value, this.msgType, this.paData, this.cname, this.ticket, this.encPart);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link KrbKdcRep#msgType() msgType} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for msgType
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableKrbKdcRep withMsgType(int value) {
    if (this.msgType == value) return this;
    return new ImmutableKrbKdcRep(this.pvno, value, this.paData, this.cname, this.ticket, this.encPart);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link KrbKdcRep#paData() paData}.
   * The array is cloned before being saved as attribute values.
   * @param elements The non-null elements for paData
   * @return A modified copy of {@code this} object
   */
  public final ImmutableKrbKdcRep withPaData(PaData... elements) {
    PaData[] newValue = elements.clone();
    return new ImmutableKrbKdcRep(this.pvno, this.msgType, newValue, this.cname, this.ticket, this.encPart);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link KrbKdcRep#cname() cname} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for cname
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableKrbKdcRep withCname(PrincipalName value) {
    if (this.cname == value) return this;
    PrincipalName newValue = Objects.requireNonNull(value, "cname");
    return new ImmutableKrbKdcRep(this.pvno, this.msgType, this.paData, newValue, this.ticket, this.encPart);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link KrbKdcRep#ticket() ticket} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for ticket
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableKrbKdcRep withTicket(Ticket value) {
    if (this.ticket == value) return this;
    Ticket newValue = Objects.requireNonNull(value, "ticket");
    return new ImmutableKrbKdcRep(this.pvno, this.msgType, this.paData, this.cname, newValue, this.encPart);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link KrbKdcRep#encPart() encPart} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for encPart
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableKrbKdcRep withEncPart(EncryptedData value) {
    if (this.encPart == value) return this;
    EncryptedData newValue = Objects.requireNonNull(value, "encPart");
    return new ImmutableKrbKdcRep(this.pvno, this.msgType, this.paData, this.cname, this.ticket, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableKrbKdcRep} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(Object another) {
    if (this == another) return true;
    return another instanceof ImmutableKrbKdcRep
        && equalTo(0, (ImmutableKrbKdcRep) another);
  }

  private boolean equalTo(int synthetic, ImmutableKrbKdcRep another) {
    return pvno == another.pvno
        && msgType == another.msgType
        && Arrays.equals(paData, another.paData)
        && cname.equals(another.cname)
        && ticket.equals(another.ticket)
        && encPart.equals(another.encPart);
  }

  /**
   * Computes a hash code from attributes: {@code pvno}, {@code msgType}, {@code paData}, {@code cname}, {@code ticket}, {@code encPart}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 5381;
    h += (h << 5) + pvno;
    h += (h << 5) + msgType;
    h += (h << 5) + Arrays.hashCode(paData);
    h += (h << 5) + cname.hashCode();
    h += (h << 5) + ticket.hashCode();
    h += (h << 5) + encPart.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code KrbKdcRep} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return "KrbKdcRep{"
        + "pvno=" + pvno
        + ", msgType=" + msgType
        + ", paData=" + Arrays.toString(paData)
        + ", cname=" + cname
        + ", ticket=" + ticket
        + ", encPart=" + encPart
        + "}";
  }

  /**
   * Utility type used to correctly read immutable object from JSON representation.
   * @deprecated Do not use this type directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Generated(from = "KrbKdcRep", generator = "Immutables")
  @Deprecated
  @JsonDeserialize
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
  static final class Json implements KrbKdcRep {
    int pvno;
    boolean pvnoIsSet;
    int msgType;
    boolean msgTypeIsSet;
    PaData[] paData;
    PrincipalName cname;
    Ticket ticket;
    EncryptedData encPart;
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
    @JsonProperty("cname")
    public void setCname(PrincipalName cname) {
      this.cname = cname;
    }
    @JsonProperty("ticket")
    public void setTicket(Ticket ticket) {
      this.ticket = ticket;
    }
    @JsonProperty("encPart")
    public void setEncPart(EncryptedData encPart) {
      this.encPart = encPart;
    }
    @Override
    public int pvno() { throw new UnsupportedOperationException(); }
    @Override
    public int msgType() { throw new UnsupportedOperationException(); }
    @Override
    public PaData[] paData() { throw new UnsupportedOperationException(); }
    @Override
    public PrincipalName cname() { throw new UnsupportedOperationException(); }
    @Override
    public Ticket ticket() { throw new UnsupportedOperationException(); }
    @Override
    public EncryptedData encPart() { throw new UnsupportedOperationException(); }
  }

  /**
   * @param json A JSON-bindable data structure
   * @return An immutable value type
   * @deprecated Do not use this method directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  static ImmutableKrbKdcRep fromJson(Json json) {
    ImmutableKrbKdcRep.Builder builder = ImmutableKrbKdcRep.builder();
    if (json.pvnoIsSet) {
      builder.pvno(json.pvno);
    }
    if (json.msgTypeIsSet) {
      builder.msgType(json.msgType);
    }
    if (json.paData != null) {
      builder.paData(json.paData);
    }
    if (json.cname != null) {
      builder.cname(json.cname);
    }
    if (json.ticket != null) {
      builder.ticket(json.ticket);
    }
    if (json.encPart != null) {
      builder.encPart(json.encPart);
    }
    return builder.build();
  }

  /**
   * Creates an immutable copy of a {@link KrbKdcRep} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable KrbKdcRep instance
   */
  public static ImmutableKrbKdcRep copyOf(KrbKdcRep instance) {
    if (instance instanceof ImmutableKrbKdcRep) {
      return (ImmutableKrbKdcRep) instance;
    }
    return ImmutableKrbKdcRep.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableKrbKdcRep ImmutableKrbKdcRep}.
   * <pre>
   * ImmutableKrbKdcRep.builder()
   *    .pvno(int) // required {@link KrbKdcRep#pvno() pvno}
   *    .msgType(int) // required {@link KrbKdcRep#msgType() msgType}
   *    .paData(messageformats.PaData) // required {@link KrbKdcRep#paData() paData}
   *    .cname(messageformats.PrincipalName) // required {@link KrbKdcRep#cname() cname}
   *    .ticket(messageformats.Ticket) // required {@link KrbKdcRep#ticket() ticket}
   *    .encPart(messageformats.EncryptedData) // required {@link KrbKdcRep#encPart() encPart}
   *    .build();
   * </pre>
   * @return A new ImmutableKrbKdcRep builder
   */
  public static ImmutableKrbKdcRep.Builder builder() {
    return new ImmutableKrbKdcRep.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableKrbKdcRep ImmutableKrbKdcRep}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @Generated(from = "KrbKdcRep", generator = "Immutables")
  public static final class Builder {
    private static final long INIT_BIT_PVNO = 0x1L;
    private static final long INIT_BIT_MSG_TYPE = 0x2L;
    private static final long INIT_BIT_PA_DATA = 0x4L;
    private static final long INIT_BIT_CNAME = 0x8L;
    private static final long INIT_BIT_TICKET = 0x10L;
    private static final long INIT_BIT_ENC_PART = 0x20L;
    private long initBits = 0x3fL;

    private int pvno;
    private int msgType;
    private PaData[] paData;
    private PrincipalName cname;
    private Ticket ticket;
    private EncryptedData encPart;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code KrbKdcRep} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(KrbKdcRep instance) {
      Objects.requireNonNull(instance, "instance");
      pvno(instance.pvno());
      msgType(instance.msgType());
      paData(instance.paData());
      cname(instance.cname());
      ticket(instance.ticket());
      encPart(instance.encPart());
      return this;
    }

    /**
     * Initializes the value for the {@link KrbKdcRep#pvno() pvno} attribute.
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
     * Initializes the value for the {@link KrbKdcRep#msgType() msgType} attribute.
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
     * Initializes the value for the {@link KrbKdcRep#paData() paData} attribute.
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
     * Initializes the value for the {@link KrbKdcRep#cname() cname} attribute.
     * @param cname The value for cname 
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("cname")
    public final Builder cname(PrincipalName cname) {
      this.cname = Objects.requireNonNull(cname, "cname");
      initBits &= ~INIT_BIT_CNAME;
      return this;
    }

    /**
     * Initializes the value for the {@link KrbKdcRep#ticket() ticket} attribute.
     * @param ticket The value for ticket 
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("ticket")
    public final Builder ticket(Ticket ticket) {
      this.ticket = Objects.requireNonNull(ticket, "ticket");
      initBits &= ~INIT_BIT_TICKET;
      return this;
    }

    /**
     * Initializes the value for the {@link KrbKdcRep#encPart() encPart} attribute.
     * @param encPart The value for encPart 
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("encPart")
    public final Builder encPart(EncryptedData encPart) {
      this.encPart = Objects.requireNonNull(encPart, "encPart");
      initBits &= ~INIT_BIT_ENC_PART;
      return this;
    }

    /**
     * Builds a new {@link ImmutableKrbKdcRep ImmutableKrbKdcRep}.
     * @return An immutable instance of KrbKdcRep
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableKrbKdcRep build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableKrbKdcRep(pvno, msgType, paData, cname, ticket, encPart);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = new ArrayList<>();
      if ((initBits & INIT_BIT_PVNO) != 0) attributes.add("pvno");
      if ((initBits & INIT_BIT_MSG_TYPE) != 0) attributes.add("msgType");
      if ((initBits & INIT_BIT_PA_DATA) != 0) attributes.add("paData");
      if ((initBits & INIT_BIT_CNAME) != 0) attributes.add("cname");
      if ((initBits & INIT_BIT_TICKET) != 0) attributes.add("ticket");
      if ((initBits & INIT_BIT_ENC_PART) != 0) attributes.add("encPart");
      return "Cannot build KrbKdcRep, some of required attributes are not set " + attributes;
    }
  }
}
