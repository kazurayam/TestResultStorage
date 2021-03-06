package com.kazurayam.materials.impl

import com.kazurayam.materials.VisualTestingLogger

//import com.kms.katalon.core.logging.KeywordLogger
//import com.kms.katalon.core.util.KeywordUtil

/**
 * The instance of DefaulVisualTestingLogger is to be registered into ImageCollectionProcessor.
 * The instance will print messages emitted by ImageCollectionProcessor.
 * The messages will be direted into System.out.
 * 
 * @author kazurayam
 */
class VisualTestingLoggerDefaultImpl implements VisualTestingLogger {

    void info(String message) {
        System.out.println("INFO " + message)
    }
    
    void failed(String message) {
        System.err.println("FAILED " + message)
    }
    
    void fatal(String message) {
        System.err.println("FATAL " + message)
    }
    
    //private KeywordLogger logger = new KeywordLogger()
    //void info(String message) {
    //    logger.logInfo(message)
    //}
    //void failed(String message) {
    //    logger.logFailed(message)
    //    KeywordUtil.markFailed(message)
    //}
    //void fatal(String message) {
    //    logger.logFailed(message)
    //    KeywordUtil.markFailedAndStop(message)
    //}
}