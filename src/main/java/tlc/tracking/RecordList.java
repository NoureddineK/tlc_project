package tlc.tracking;

import java.util.ArrayList;

// Workaround for Restlet+Jackson: https://stackoverflow.com/a/12994065
public class RecordList extends ArrayList<Record> {
    public RecordList() {}
}