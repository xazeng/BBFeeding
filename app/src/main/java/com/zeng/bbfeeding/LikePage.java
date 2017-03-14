package com.zeng.bbfeeding;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;

/**
 * Created by xianganzeng on 2016/10/27.
 */

public class LikePage extends Page{
    @Override
    protected void onCreatePage(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.page_like);

    }
}
