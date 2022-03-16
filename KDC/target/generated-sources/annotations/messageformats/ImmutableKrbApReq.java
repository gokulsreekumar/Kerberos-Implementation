package messageformats;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.immutables.value.Generated;

/**
 * Immutable implementation of {@link KrbApReq}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableKrbApReq.builder()}.
 */
@Generated(from = "KrbApReq", generator = "Immutables")
@SuppressWarnings({"all"})
@javax.annotation.processing.Generated("org.immutables.processor.ProxyProcessor")
public final class ImmutableKrbApReq implements KrbApReq {
  private final int pvno;
  private final int msgType;
  private final Ticket ticket;
  private final EncryptedData authenticator;

  private ImmutableKrbApReq() {
    this.pvno = 0;
    this.msgType = 0;
    this.ticket = null;
    this.authenticator = null;
  }

  private ImmutableKrbApReq(int pvno, int msgType, Ticket ticket, EncryptedData authenticator) {
    this.pvno = pvno;
    this.msgType = msgType;
    this.ticket = ticket;
    this.authenticator = authenticator;
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
   * @return The value of the {@code ticket} attribute
   */
  @JsonProperty("ticket")
  @Override
  public Ticket ticket() {
    return ticket;
  }

  /**
   * @return The value of the {@code authenticator} attribute
   */
  @JsonProperty("authenticator")
  @Override
  public EncryptedData authenticator() {
    return authenticator;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link KrbApReq#pvno() pvno} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for pvno
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableKrbApReq withPvno(int value) {
    if (this.pvno == value) return this;
    return new ImmutableKrbApReq(value, this.msgType, this.ticket, this.authenticator);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link KrbApReq#msgType() msgType} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for msgType
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableKrbApReq withMsgType(int value) {
    if (this.msgType == value) return this;
    return new ImmutableKrbApReq(this.pvno, value, this.ticket, this.authenticator);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link KrbApReq#ticket() ticket} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for ticket
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableKrbApReq withTicket(Ticket value) {
    if (this.ticket == value) return this;
    Ticket newValue = Objects.requireNonNull(value, "ticket");
    return new ImmutableKrbApReq(this.pvno, this.msgType, newValue, this.authenticator);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link KrbApReq#authenticator() authenticator} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for authenticator
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableKrbApReq withAuthenticator(EncryptedData value) {
    if (this.authenticator == value) return this;
    EncryptedData newValue = Objects.requireNonNull(value, "authenticator");
    return new ImmutableKrbApReq(this.pvno, this.msgType, this.ticket, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableKrbApReq} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(Object another) {
    if (this == another) return true;
    return another instanceof ImmutableKrbApReq
        && equalTo(0, (ImmutableKrbApReq) another);
  }

  private boolean equalTo(int synthetic, ImmutableKrbApReq another) {
    return pvno == another.pvno
        && msgType == another.msgType
        && ticket.equals(another.ticket)
        && authenticator.equals(another.authenticator);
  }

  /**
   * Computes a hash code from attributes: {@code pvno}, {@code msgType}, {@code ticket}, {@code authenticator}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 5381;
    h += (h << 5) + pvno;
    h += (h << 5) + msgType;
    h += (h << 5) + ticket.hashCode();
    h += (h << 5) + authenticator.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code KrbApReq} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return "KrbApReq{"
        + "pvno=" + pvno
        + ", msgType=" + msgType
        + ", ticket=" + ticket
        + ", authenticator=" + authenticator
        + "}";
  }

  /**
   * Utility type used to correctly read immutable object from JSON representation.
   * @deprecated Do not use this type directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Generated(from = "KrbApReq", generator = "Immutables")
  @Deprecated
  @JsonDeserialize
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
  static final class Json implements KrbApReq {
    int pvno;
    boolean pvnoIsSet;
    int msgType;
    boolean msgTypeIsSet;
    Ticket ticket;
    EncryptedData authenticator;
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
    @JsonProperty("ticket")
    public void setTicket(Ticket ticket) {
      this.ticket = ticket;
    }
    @JsonProperty("authenticator")
    public void setAuthenticator(EncryptedData authenticator) {
      this.authenticator = authenticator;
    }
    @Override
    public int pvno() { throw new UnsupportedOperationException(); }
    @Override
    public int msgType() { throw new UnsupportedOperationException(); }
    @Override
    public Ticket ticket() { throw new UnsupportedOperationException(); }
    @Override
    public EncryptedData authenticator() { throw new UnsupportedOperationException(); }
  }

  /**
   * @param json A JSON-bindable data structure
   * @return An immutable value type
   * @deprecated Do not use this method directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  static ImmutableKrbApReq fromJson(Json json) {
    ImmutableKrbApReq.Builder builder = ImmutableKrbApReq.builder();
    if (json.pvnoIsSet) {
      builder.pvno(json.pvno);
    }
    if (json.msgTypeIsSet) {
      builder.msgType(json.msgType);
    }
    if (json.ticket != null) {
      builder.ticket(json.ticket);
    }
    if (json.authenticator != null) {
      builder.authenticator(json.authenticator);
    }
    return builder.build();
  }

  /**
   * Creates an immutable copy of a {@link KrbApReq} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable KrbApReq instance
   */
  public static ImmutableKrbApReq copyOf(KrbApReq instance) {
    if (instance instanceof ImmutableKrbApReq) {
      return (ImmutableKrbApReq) instance;
    }
    return ImmutableKrbApReq.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableKrbApReq ImmutableKrbApReq}.
   * <pre>
   * ImmutableKrbApReq.builder()
   *    .pvno(int) // required {@link KrbApReq#pvno() pvno}
   *    .msgType(int) // required {@link KrbApReq#msgType() msgType}
   *    .ticket(messageformats.Ticket) // required {@link KrbApReq#ticket() ticket}
   *    .authenticator(messageformats.EncryptedData) // required {@link KrbApReq#authenticator() authenticator}
   *    .build();
   * </pre>
   * @return A new ImmutableKrbApReq builder
   */
  public static ImmutableKrbApReq.Builder builder() {
    return new ImmutableKrbApReq.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableKrbApReq ImmutableKrbApReq}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @Generated(from = "KrbApReq", generator = "Immutables")
  public static final class Builder {
    private static final long INIT_BIT_PVNO = 0x1L;
    private static final long INIT_BIT_MSG_TYPE = 0x2L;
    private static final long INIT_BIT_TICKET = 0x4L;
    private static final long INIT_BIT_AUTHENTICATOR = 0x8L;
    private long initBits = 0xfL;

    private int pvno;
    private int msgType;
    private Ticket ticket;
    private EncryptedData authenticator;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code KrbApReq} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(KrbApReq instance) {
      Objects.requireNonNull(instance, "instance");
      pvno(instance.pvno());
      msgType(instance.msgType());
      ticket(instance.ticket());
      authenticator(instance.authenticator());
      return this;
    }

    /**
     * Initializes the value for the {@link KrbApReq#pvno() pvno} attribute.
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
     * Initializes the value for the {@link KrbApReq#msgType() msgType} attribute.
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
     * Initializes the value for the {@link KrbApReq#ticket() ticket} attribute.
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
     * Initializes the value for the {@link KrbApReq#authenticator() authenticator} attribute.
     * @param authenticator The value for authenticator 
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("authenticator")
    public final Builder authenticator(EncryptedData authenticator) {
      this.authenticator = Objects.requireNonNull(authenticator, "authenticator");
      initBits &= ~INIT_BIT_AUTHENTICATOR;
      return this;
    }

    /**
     * Builds a new {@link ImmutableKrbApReq ImmutableKrbApReq}.
     * @return An immutable instance of KrbApReq
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableKrbApReq build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableKrbApReq(pvno, msgType, ticket, authenticator);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = new ArrayList<>();
      if ((initBits & INIT_BIT_PVNO) != 0) attributes.add("pvno");
      if ((initBits & INIT_BIT_MSG_TYPE) != 0) attributes.add("msgType");
      if ((initBits & INIT_BIT_TICKET) != 0) attributes.add("ticket");
      if ((initBits & INIT_BIT_AUTHENTICATOR) != 0) attributes.add("authenticator");
      return "Cannot build KrbApReq, some of required attributes are not set " + attributes;
    }
  }
}
