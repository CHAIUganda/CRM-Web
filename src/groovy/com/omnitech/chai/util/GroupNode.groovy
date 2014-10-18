package com.omnitech.chai.util

/**
 * Created by kay on 10/18/14.
 */

interface HasId {
    def getId()
}

interface HasName {
    String getName()
}

interface GroupNode extends HasId, HasName {
    GroupNode getParent()
}

interface LeafNode extends HasId, HasName {
    GroupNode getParent()
}

