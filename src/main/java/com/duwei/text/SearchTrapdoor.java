package com.duwei.text;

import com.duwei.param.PublicParams;
import com.duwei.text.transportable.TransportableSearchTrapdoor;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.text
 * @Author: duwei
 * @Date: 2023/5/10 10:30
 * @Description: 搜搜陷门
 */
@Data
@NoArgsConstructor
public class SearchTrapdoor {
    private Element T1;
    private Element T2;
    private Element T3;
    private Element T4;
    private int keywordNumber;

    public SearchTrapdoor(Element t1, Element t2, Element t3, Element t4,int keywordNumber) {
        T1 = t1;
        T2 = t2;
        T3 = t3;
        T4 = t4;
        this.keywordNumber = keywordNumber;
    }

    public static SearchTrapdoor rebuild(TransportableSearchTrapdoor transportableSearchTrapdoor, PublicParams publicParams){
        Field G0 = publicParams.getCurveElementParams().getG0();
        SearchTrapdoor searchTrapdoor = new SearchTrapdoor();
        searchTrapdoor.setT1(G0.newElementFromBytes(transportableSearchTrapdoor.getT1()).getImmutable());
        searchTrapdoor.setT2(G0.newElementFromBytes(transportableSearchTrapdoor.getT2()).getImmutable());
        searchTrapdoor.setT3(G0.newElementFromBytes(transportableSearchTrapdoor.getT3()).getImmutable());
        searchTrapdoor.setT4(G0.newElementFromBytes(transportableSearchTrapdoor.getT4()).getImmutable());
        searchTrapdoor.setKeywordNumber(transportableSearchTrapdoor.getKeywordNumber());
        return searchTrapdoor;
    }
}
