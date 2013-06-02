package org.black_ixx.playerpoints.services;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents pre-release version types.
 * 
 * @author Mitsugaru
 */
public class PreReleaseType implements Comparable<PreReleaseType> {

    /**
     * Empty prerelease type.
     */
    public static final PreReleaseType NONE = new PreReleaseType("NONE");

    /**
     * Normal string found in version.
     */
    private final String type;

    /**
     * Base pre-release type.
     */
    private String base;

    /**
     * List of identifiers after the base type.
     */
    private final List<String> identifiers = new ArrayList<String>();

    /**
     * Private constructor.
     * 
     * @param in
     *            - Type's normal string.
     */
    public PreReleaseType(final String in) {
        this.type = in;
        try {
            this.base = in.substring(0, in.indexOf("."));
        } catch(IndexOutOfBoundsException e) {
            this.base = in;
        }

        String[] ids = in.split("\\.");
        for(int i = 1; i < ids.length; i++) {
            identifiers.add(ids[i]);
        }
    }

    public String getType() {
        return type;
    }

    public String getBase() {
        return base;
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }

    @Override
    public String toString() {
        return type;
    }

    /**
     * Standard order of pre-release types, from latest to earliest.
     */
    private enum Order {
        NONE,
        RC,
        SNAPSHOT,
        BETA,
        ALPHA;
    }

    @Override
    public int compareTo(PreReleaseType o) {
        int compare = 0;

        // Compare base types
        try {
            Order o1 = Order.valueOf(base.toUpperCase());
            Order o2 = Order.valueOf(o.getBase().toUpperCase());
            compare = o1.ordinal() - o2.ordinal();
        } catch(IllegalArgumentException e) {
            compare = type.compareTo(o.getType());
        }

        // Compare identifiers if matching base types
        if(compare == 0) {
            int max = Math.max(identifiers.size(), o.getIdentifiers().size());
            for(int i = 0; i < max; i++) {
                String ours = "";
                try {
                    ours = identifiers.get(i);
                } catch(IndexOutOfBoundsException e) {
                    compare = 1;
                    break;
                }
                String theirs = "";
                try {
                    theirs = o.getIdentifiers().get(i);
                } catch(IndexOutOfBoundsException e) {
                    compare = -1;
                    break;
                }
                if(theirs.compareTo(ours) != 0) {
                    compare = theirs.compareTo(ours);
                    break;
                }
            }
        }

        return compare;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PreReleaseType) {
            return type.equals(((PreReleaseType) obj).getType());
        }
        return false;
    }

}
