package ao.uan.fc.dam.mobile.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseExecutor {

    public static final ExecutorService executor =
            Executors.newSingleThreadExecutor();

}