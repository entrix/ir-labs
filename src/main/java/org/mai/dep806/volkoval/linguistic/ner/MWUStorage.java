package org.mai.dep806.volkoval.linguistic.ner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 19.06.13
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 */
public class MWUStorage {
    Set<MWU> mwuSet = new HashSet<>();

    public void addMWU(MWU mwu) {
        this.mwuSet.add(mwu);
    }

    public boolean contains(MWU mwu) {
        return mwuSet.contains(mwu);
    }

    public List<MWU> getAll() {
        List<MWU> mwuList = new ArrayList<>();

        for (MWU mwu : mwuSet) {
            mwuList.add(mwu);
        }

        return mwuList;
    }
}
