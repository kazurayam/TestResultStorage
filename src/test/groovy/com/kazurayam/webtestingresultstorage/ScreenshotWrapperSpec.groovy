package com.kazurayam.webtestingresultstorage

import java.nio.file.Path
import java.nio.file.Paths

import spock.lang.Specification

//@Ignore
class ScreenshotWrapperSpec extends Specification {

    // fields
    private static Path workdir
    private static Path fixture = Paths.get("./src/test/fixture/Screenshots")

    // fixture methods
    def setup() {
        workdir = Paths.get("./build/tmp/${Helpers.getClassShortName(ScreenshotWrapperSpec.class)}")
        if (!workdir.toFile().exists()) {
            workdir.toFile().mkdirs()
        }

    }

    // feature methods
    def testToString() {
        setup:
        String dirName = 'testToString'
        Path baseDir = workdir.resolve(dirName)
        Helpers.ensureDirs(baseDir)
        Helpers.copyDirectory(fixture, baseDir)
        when:
        TestSuiteName tsn = new TestSuiteName('TS1')
        TestCaseName tcn = new TestCaseName('TC1')
        TestSuiteTimestamp tstamp = new TestSuiteTimestamp('20180530_130419')
        WebTestingResultStorageImpl wtrs = new WebTestingResultStorageImpl(baseDir, tsn)
        TestSuiteResult tsr = wtrs.getTestSuiteResult(tsn, tstamp)
        TestCaseResult tcr = tsr.getTestCaseResult(tcn)
        assert tcr != null
        TargetPage tp = tcr.getTargetPage(new URL('http://demoaut.katalon.com/'))
        ScreenshotWrapper sw = tp.getScreenshotWrapper('')
        def str = sw.toString()
        then:
        str.startsWith('{"ScreenshotWrapper":{"screenshotFilePath":"')
        str.contains(sw.getScreenshotFilePath().toString())
        str.endsWith('"}}')
    }
}
