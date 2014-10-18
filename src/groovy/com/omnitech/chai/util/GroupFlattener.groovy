package com.omnitech.chai.util


/**
 * Created by kay on 1/14/14.
 */
class GroupFlattener {

    List<LeafNode> leaves = []

    Map<String, NodeRecord> reportRecords = [:]

    List<NodeRecord> normalize() {
        for (report in leaves) {
            processReport(report)
        }

        return organise().values().asList()
    }

    private Map<String, NodeRecord> organise() {
        def heads = reportRecords.findResults { k, v ->
            if (v.isHead()) return v
            return null
        }

        def finalResults = [:]

        heads.each {
            finalResults.putAll(it.getAll())
        }

        return finalResults
    }

    private NodeRecord processReport(LeafNode leaf) {
        def group = leaf.getParent();

        NodeRecord groupRecord

        if (group) {
            groupRecord = processGroup(group)
        }

        def reportRecord = addReportRecord(leaf)
        if (groupRecord) {
            reportRecord.setParent(groupRecord)
        }
        reportRecord
    }

    private NodeRecord processGroup(GroupNode group) {

        def childGrpRecord = addGroupRecord(group)

        if (group.parent) {
            def parentRecord = processGroup(group.parent)
            childGrpRecord.setParent(parentRecord)
        }
        return childGrpRecord

    }

    private NodeRecord addGroupRecord(GroupNode group) {
        def record = new NodeRecord(
                id: group.id,
                parentId: group.parent?.id,
                name: group.name,
                element: group,
        )
        addRecord(record)
    }

    private NodeRecord addReportRecord(LeafNode leaf) {
        def record = new NodeRecord(
                id: leaf.id,
                parentId: leaf.parent?.id,
                name: leaf.name,
                element: leaf
        )
        addRecord(record)
    }

    private NodeRecord addRecord(NodeRecord record) {
        def oldRecord = reportRecords[record.id]

        if (oldRecord)
            return oldRecord

        reportRecords[record.id] = record
        return record
    }
}
