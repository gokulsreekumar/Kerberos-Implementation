package messageformats;

@Value.Immutable
public class PrincipalName {
    /*
        This field encodes a sequence of components that form a name, each component encoded as a KerberosString.
        Taken together, a PrincipalName and a Realm form a principal identifier.
     */
    private String nameString;

    public PrincipalName(String nameString) {
        this.nameString = nameString;
    }

    public String getNameString() {
        return nameString;
    }

    public void setNameString(String nameString) {
        this.nameString = nameString;
    }
}
