package com.omnitech.chai.util

import org.codehaus.groovy.runtime.InvokerHelper

/**
 * Created by kay on 1/14/14.
 */
class NodeRecord<T> {

    NodeRecord parent
    T element
    Map<String, NodeRecord> children = [:]


    String id
    String parentId
    String name


    boolean isGroup() {
        return !children.isEmpty()
    }

    boolean isLeaf() {
        return children.isEmpty()
    }


    String toRowString() {
        return "${name.patchUp(30)}${element.getClass().simpleName}"
    }

    void setParent(NodeRecord parent) {
        this.parent = parent
        parent.addChild(this)

    }

    void addChild(NodeRecord child) {
        children[child.id] = child
    }

    Map<String, NodeRecord> getAll() {
        def finalMap = ["$id": this]
        finalMap.putAll(getAllChildren())
        return finalMap
    }

    def methodMissing(String name, def args) {
        return null
    }

    def propertyMissing(String name) {
        InvokerHelper.getProperty(element,name)
    }

    def propertyMissing(String name, def arg) {
        InvokerHelper.invokeMethod(element, name, arg)
    }

    Map<String, NodeRecord> getAllChildren() {
        if (children.isEmpty())
            return [:]
        def finalMap = [:]

        children.each { k, v ->
            finalMap[v.id] = v
            if (v.children) {
                finalMap.putAll(v.allChildren)
            }
        }
        return finalMap
    }

    boolean isHead() {
        return parent == null
    }


    String toString() {
        name
    }
}
