package messageformats;

//import org.immutables.value.Value;
//
//@Value.Immutable
//public interface PrincipalName {
//    String nameString();
//}

import java.io.Serializable;

public class PrincipalName implements Serializable {
    String nameString;

    // For Deserialization
    public PrincipalName() {
    }

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
