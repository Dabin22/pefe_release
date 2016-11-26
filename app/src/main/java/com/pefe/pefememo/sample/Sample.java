package com.pefe.pefememo.sample;

import com.pefe.pefememo.model.directory.Directory;

/**
 * Created by dodoproject on 2016-11-25.
 */

public class Sample {
    private static Directory sampleDirectory;
    public Sample(){
        sampleDirectory = new Directory();
        sampleDirectory.setNo(1);
        sampleDirectory.setCode("true_dir_01");
        sampleDirectory.setName("first_Dir");
        sampleDirectory.setPw("12332323");
    }
}
