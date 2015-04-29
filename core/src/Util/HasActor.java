package Util;

import fvs.taxe.actor.GenericActor;
import gameLogic.resource.Train;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.io.Serializable;


/**
 * Created by alfioemanuele on 3/2/15.
 */
public class HasActor<Type> implements Serializable {
    String myUniqueID;

    private void generateRandomUniqueID() {
        do {
            SecureRandom random = new SecureRandom();
            myUniqueID = new BigInteger(130, random).toString(32);
        } while ( ActorsManager.containsKey(myUniqueID) );
    }

    private void ensureRandomUniqueID() {
        if ( myUniqueID == null ) {
            generateRandomUniqueID();
        }
    }

    public Type getActor() {
        ensureRandomUniqueID();
        return (Type) ActorsManager.get(myUniqueID);
    }

    public void setActor(Type a) {
        ensureRandomUniqueID();
        if ( this instanceof Train) {
            ActorsManager.addTrainActor((GenericActor) a);
        }
        ActorsManager.put(myUniqueID, (GenericActor) a);
    }

}
