package com.battlehack_venice.mecenate;

import android.content.Context;

import com.battlehack_venice.lib.api.ApiClient;

import org.slf4j.LoggerFactory;

import java.io.File;

import javax.inject.Inject;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;

public class Application extends android.support.multidex.MultiDexApplication
{
    private static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Application.class);

    private static final String LOG_FILENAME = "log.0.txt";
    private static final String LOG_ROLLING = "log.%i.txt";
    private static final String LOG_PATTERN = "%date{ISO8601} [%thread] %-5level %logger{20} - %msg%n";

    private static Context context;
    private static ApplicationComponent appComponent;

    @Inject
    ApiClient _apiClient;

    @Override
    public void onCreate()
    {
        super.onCreate();

        Application.context = getApplicationContext();

        // Initialize stuff so their instances
        // are bound to the application process.
        @SuppressWarnings("deprecation")
        File logsDir = this.getApplicationContext().getDir("logs", Context.MODE_WORLD_READABLE);
        File logFile = new File(logsDir, LOG_FILENAME);

        // Init
        this._initLogger(logsDir, logFile);

        // Initalize app component (dependency injector)
        Application.appComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        appComponent.inject(this);
    }

    public static final ApplicationComponent injector()
    {
        if (appComponent == null) {
            throw new IllegalStateException("Global app injector should be initialised in onCreate()");
        }

        return appComponent;
    }

    private void _initLogger(File logsDir, File logFile)
    {
        // reset the default context (which may already have been initialized)
        // since we want to reconfigure it
        final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.reset();

        // setup FileAppender
        PatternLayoutEncoder fileLayoutEncoder = new PatternLayoutEncoder();
        fileLayoutEncoder.setContext(lc);
        fileLayoutEncoder.setPattern(LOG_PATTERN);
        fileLayoutEncoder.start();

        RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<ILoggingEvent>();
        FixedWindowRollingPolicy fileRolling = new FixedWindowRollingPolicy();
        fileRolling.setMinIndex(1);
        fileRolling.setMaxIndex(1);
        fileRolling.setFileNamePattern(new File(logsDir, LOG_ROLLING).getAbsolutePath());
        fileRolling.setContext(lc);
        fileRolling.setParent(fileAppender);

        SizeBasedTriggeringPolicy<ILoggingEvent> fileTriggering = new SizeBasedTriggeringPolicy<ILoggingEvent>("250KB");
        fileTriggering.setContext(lc);

        fileAppender.setContext(lc);
        fileAppender.setFile(logFile.getAbsolutePath());
        fileAppender.setEncoder(fileLayoutEncoder);
        fileAppender.setAppend(true);
        fileAppender.setPrudent(false);
        fileAppender.setTriggeringPolicy(fileTriggering);
        fileAppender.setRollingPolicy(fileRolling);

        fileRolling.start();
        fileTriggering.start();
        fileAppender.start();

        // setup LogcatAppender
        PatternLayoutEncoder logcatLayoutEncoder = new PatternLayoutEncoder();
        logcatLayoutEncoder.setContext(lc);
        logcatLayoutEncoder.setPattern("[%thread] %msg%n");
        logcatLayoutEncoder.start();

        LogcatAppender logcatAppender = new LogcatAppender();
        logcatAppender.setContext(lc);
        logcatAppender.setEncoder(logcatLayoutEncoder);
        logcatAppender.start();

        // config levels
        ((Logger) LoggerFactory.getLogger("com.spreaker")).setLevel(BuildConfig.DEBUG ? Level.DEBUG : Level.INFO);

        // enqueue the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(logcatAppender);
        root.addAppender(fileAppender);
    }
}