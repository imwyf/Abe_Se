package com.duwei.sample;

import com.duwei.util.ComputeUtils;

import java.util.*;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.sample
 * @Author: duwei
 * @Date: 2023/5/10 11:11
 * @Description: 从固定列表元素中抽样出指定个数, 且不重复
 */
public class FixedSample<T> implements Sample<T> {
    //记录所有采用结果集
    private List<List<T>> sampleResultSets;
    //每次抽样个数
    private int eachSampleNumber;
    //最大抽样种类
    private int maxSampleKinds;
    //记录当前是第几次抽样
    private int curSampleNo;

    public FixedSample(List<T> originalSampleList,int eachSampleNumber) {
        //原始样本集合
        this.eachSampleNumber = eachSampleNumber;
        this.maxSampleKinds = ComputeUtils.permutationNumber(originalSampleList.size(),eachSampleNumber);
        this.curSampleNo = 0;
        this.sampleResultSets = new ArrayList<>();
        dfs(0,originalSampleList,eachSampleNumber,new ArrayList<>(),sampleResultSets);
    }

    public void dfs(int index, List<T> originalSampleList, int restSample, List<T> preSelect, List<List<T>> result){
        if (index == originalSampleList.size()){
            if (restSample == 0){
                result.add(new ArrayList<>(preSelect));
            }
            return;
        }

        if (restSample == 0){
            result.add(new ArrayList<>(preSelect));
            return;
        }

        int hasSampleNumber = originalSampleList.size() - index;
        if (hasSampleNumber < restSample){
            return;
        }

        //1.要当前的
        preSelect.add(originalSampleList.get(index));
        dfs(index + 1,originalSampleList,restSample - 1,preSelect,result);

        //2.不要当前的
        preSelect.remove(preSelect.size() - 1);
        dfs(index + 1,originalSampleList,restSample,preSelect,result);
    }


    @Override
    public List<T> next() {
        return sampleResultSets.get(curSampleNo++);
    }

    @Override
    public boolean hasNext() {
        return curSampleNo < maxSampleKinds;
    }

}
