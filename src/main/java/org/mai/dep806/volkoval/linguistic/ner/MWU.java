package org.mai.dep806.volkoval.linguistic.ner;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 19.06.13
 * Time: 14:06
 * To change this template use File | Settings | File Templates.
 */
public class MWU {

    private List<String> mwu;

    public MWU(List<String> mwu) {
        this.mwu = mwu;
    }

    public List<String> getAll() {
        return mwu;
    }

    @Override
    public int hashCode() {
        if (mwu == null) {
            return super.hashCode();
        }

        int sum = 0;

        for (String word : mwu) {
            sum += word.hashCode();
        }

        sum += mwu.hashCode();

        return sum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MWU mwu1 = (MWU) o;

        for (String s : mwu1.getAll()) {
            if (!mwu.contains(s)) {
                return false;
            }
        }

        return true;
    }
}
