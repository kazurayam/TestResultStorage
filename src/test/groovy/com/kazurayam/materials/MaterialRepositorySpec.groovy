package com.kazurayam.materials

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import groovy.util.AntBuilder
import groovy.json.JsonOutput
import spock.lang.IgnoreRest
import spock.lang.Specification

//@Ignore
class MaterialRepositorySpec extends Specification {

    static Logger logger_ = LoggerFactory.getLogger(MaterialRepositorySpec.class);

    private static Path workdir_
    private static Path fixture_ = Paths.get("./src/test/fixture")
    private static MaterialRepository mr_

    def setupSpec() {
        workdir_ = Paths.get("./build/tmp/testOutput/${Helpers.getClassShortName(MaterialRepositorySpec.class)}")
        if (workdir_.toFile().exists()) {
            Helpers.deleteDirectoryContents(workdir_)
        } else {
            workdir_.toFile().mkdirs()
        }
        def ant = new AntBuilder()
        ant.copy(todir:workdir_.toFile(), overwrite:'yes') {
            fileset(dir:fixture_) {
                exclude(name:'Materials/CURA.twins_capture/**')
                exclude(name:'Materials/CURA.twins_exam/**')
            }
        }
    }

    def setup() {
        mr_ = MaterialRepositoryFactory.createInstance(workdir_.resolve('Materials'))
    }

    def testPutCurrentTestSuite_oneStringArg() {
        when:
        mr_.putCurrentTestSuite('oneStringArg')
        Path timestampdir = mr_.getCurrentTestSuiteDirectory()
        logger_.debug("#testSetCurrentTestSuite_oneStringArg timestampdir=${timestampdir}")
        then:
        timestampdir.toString().contains('oneStringArg')
        when:
        String dirName = timestampdir.getFileName()
        LocalDateTime ldt = TSuiteTimestamp.parse(dirName)
        then:
        true
    }

    def testPutCurrentTestSuite_twoStringArg() {
        when:
        mr_.putCurrentTestSuite('oneStringArg', '20180616_160000')
        Path timestampdir = mr_.getCurrentTestSuiteDirectory()
        logger_.debug("#testSetCurrentTestSuite_oneStringArg timestampdir=${timestampdir}")
        then:
        timestampdir.toString().contains('oneStringArg')
        timestampdir.getFileName().toString().contains('20180616_160000')
    }

    def testPutCurrentTestSuite_tSuiteName_tSuiteTimestamp() {
        when:
        mr_.putCurrentTestSuite(
                new TSuiteName('oneStringArg'), TSuiteTimestamp.newInstance('20180616_160000'))
        Path timestampdir = mr_.getCurrentTestSuiteDirectory()
        logger_.debug("#testSetCurrentTestSuite_oneStringArg timestampdir=${timestampdir}")
        then:
        timestampdir.toString().contains('oneStringArg')
        timestampdir.getFileName().toString().contains('20180616_160000')
    }

    def testJsonOutput() {
        setup:
        List<String> errors = []
        errors << 'Your password was bad.'
        errors << 'Your feet smell.'
        errors << 'I am having a bad day.'
        when:
        def jsonString = JsonOutput.toJson(errors)
        then:
        jsonString == '["Your password was bad.","Your feet smell.","I am having a bad day."]'
    }

    def testToJsonText() {
        when:
        mr_.putCurrentTestSuite('Test Suites/TS1')
        def text = mr_.toJsonText()
        logger_.debug("#testToJsonText text=" + text)
        logger_.debug("#testToJsonText prettyPrint(text)=" + JsonOutput.prettyPrint(text))
        then:
        text.contains('TS1')
    }

    def testConstructor_Path_tsn() {
        when:
        mr_.putCurrentTestSuite('Test Suites/main/TS1')
        String str = mr_.toString()
        then:
        str.contains('main.TS1')
    }


    def testGetCurrentTestSuiteDirectory() {
        when:
        mr_.putCurrentTestSuite('Test Suites/main/TS1','20180530_130419')
        Path testSuiteDir = mr_.getCurrentTestSuiteDirectory()
        then:
        testSuiteDir == workdir_.resolve('Materials/main.TS1').resolve('20180530_130419').normalize()
    }
    
    def testGetSetOfMaterialPathRelativeToTSuiteTimestamp() {
        when:
        TSuiteName tsn = new TSuiteName('Test Suites/main/TS1')
        Set<Path> paths = mr_.getSetOfMaterialPathRelativeToTSuiteTimestamp(tsn)
        logger_.debug( "#testGetSetOfMaterialPathRelativeToTSuiteTimestmp: " + paths)
        then:
        paths.size() == 16
    }

    def testGetTCaseResult() {
        when:
        TCaseResult tCaseResult = mr_.getTCaseResult(new TSuiteName('Test Suites/main/TS1'),
                                        new TSuiteTimestamp('20180530_130419'),
                                        new TCaseName('Test Cases/main/TC1'))
        then:
        tCaseResult != null    
    }
    
    def testGetTestCaseDirectory() {
        when:
        mr_.putCurrentTestSuite('Test Suites/main/TS1','20180530_130419')
        Path testCaseDir = mr_.getTestCaseDirectory('Test Cases/main/TC1')
        then:
        testCaseDir == workdir_.resolve('Materials/main.TS1').resolve('20180530_130419').resolve('main.TC1').normalize()
    }
    

    def testGetTSuiteNameList() {
        when:
        List<TSuiteName> tsnList = mr_.getTSuiteNameList()
        then:
        tsnList.size() == 8
    }
    
    def testGetTSuiteResult_withTSuiteNameAndTSuiteTimestamp() {
        when:
        TSuiteName tsn = new TSuiteName('Test Suites/main/TS1')
        TSuiteTimestamp tst = TSuiteTimestamp.newInstance('20180530_130419')
        TSuiteResultId tsri = TSuiteResultId.newInstance(tsn, tst)
        mr_.putCurrentTestSuite(tsri)
        TSuiteResult tsr = mr_.getTSuiteResult(tsri)
        then:
        tsr != null
        tsr.getId().getTSuiteName().equals(tsn)
        tsr.getId().getTSuiteTimestamp().equals(tst)
        
    }
    
    def testGetTSuiteResultList_withTSuiteName() {
        when:
        List<TSuiteResultId> tsriList = mr_.getTSuiteResultIdList(new TSuiteName('Test Suites/main/TS1'))
        List<TSuiteResult> list = mr_.getTSuiteResultList(tsriList)
        then:
        list != null
        list.size() == 6
    }
    
    def testGetTSuiteResultIdList_withTSuiteName() {
        when:
        List<TSuiteResultId> list = mr_.getTSuiteResultIdList(new TSuiteName('Test Suites/main/TS1'))
        then:
        list != null
        list.size() == 6
    }
    
    def testGetTSuiteResultIdList() {
        when:
        List<TSuiteResultId> list = mr_.getTSuiteResultIdList()
        then:
        list != null
        list.size()== 15
    }
    
    def testGetTSuiteResultList_noArgs() {
        when:
        List<TSuiteResult> list = mr_.getTSuiteResultList()
        then:
        list != null
        list.size() == 15
    }

    def testResolveScreenshotPath() {
        when:
        mr_.putCurrentTestSuite('Test Suites/main/TS1','20180530_130419')
        Path path = mr_.resolveScreenshotPath('Test Cases/main/TC1', new URL('http://demoaut.katalon.com/'))
        then:
        path.getFileName().toString() == 'http%3A%2F%2Fdemoaut.katalon.com%2F(2).png'
    }
	
	def testResolveScreenshotPathByURLPathComponents_top() {
		when:
			mr_.putCurrentTestSuite('Test Suites/main/TS1','20180530_130419')
			Path path = mr_.resolveScreenshotPathByURLPathComponents(
							'Test Cases/main/TC1', new URL('http://demoaut.katalon.com/'), 0, 'top')
		then:
			path.getFileName().toString()== 'top.png'
	}

	def testResolveScreenshotPathByURLPathComponents_login() {
		when:
			mr_.putCurrentTestSuite('Test Suites/main/TS1','20180530_130419')
			Path path = mr_.resolveScreenshotPathByURLPathComponents(
							'Test Cases/main/TC1', new URL('https://katalon-demo-cura.herokuapp.com/profile.php#login'))
		then:
			path.getFileName().toString()== 'profile.php%23login.png'
	}

    def testMakeIndex() {
        when:
        mr_.putCurrentTestSuite('Test Suites/main/TS1','20180530_130419')
        Path index = mr_.makeIndex()
        then:
        Files.exists(index)
    }

   
    def testCreateMaterialPairs_TSuiteNameOnly() {
        when:
        List<MaterialPair> list = mr_.createMaterialPairs(new TSuiteName('TS1'))
        then:
        list.size() == 1
        when:
        MaterialPair mp = list.get(0)
        Material expected = mp.getExpected()
        Material actual = mp.getActual()
        then:
        expected.getPathRelativeToTSuiteTimestamp() == Paths.get('TC1/CURA_Healthcare_Service.png')
        actual.getPathRelativeToTSuiteTimestamp()   == Paths.get('TC1/CURA_Healthcare_Service.png')
    }
    

    /**
     * This will test deleteBaseDirContents() method.
     * This will create a fixture for its own.
     *
     * @throws IOException
     */
    def testDeleteBaseDirContents() throws IOException {
        setup:
        Path casedir = workdir_.resolve("testDeleteBaseDirContents")
        if (!Files.exists(casedir)) {
            Files.createDirectories(casedir)
        }
        Helpers.copyDirectory(fixture_, casedir)
        //
        MaterialRepository mr = MaterialRepositoryFactory.createInstance(casedir.resolve('Materials'))
        //
        when:
        mr.deleteBaseDirContents()
        List<String> contents = mr.getBaseDir().toFile().list()
        then:
        contents.size() == 0
    }

    def testClear_withArgTSuiteTimestamp() {
        setup:
        Path casedir = workdir_.resolve("testClear_withArgTSuiteTimestamp")
        if (!Files.exists(casedir)) {
            Files.createDirectories(casedir)
        }
        Helpers.copyDirectory(fixture_, casedir)
        MaterialRepository mr = MaterialRepositoryFactory.createInstance(casedir.resolve('Materials'))
        when:
        TSuiteName tsn = new TSuiteName("Test Suites/main/TS1")
        TSuiteTimestamp tst = TSuiteTimestamp.newInstance("20180530_130419")
        TSuiteResultId tsri = TSuiteResultId.newInstance(tsn, tst)
        int count = mr.clear(tsri)
        then:
        count == 2
        when:
        TSuiteResult result= mr.getTSuiteResult(tsri)
        then:
        result == null
        when:
        mr.putCurrentTestSuite(tsri)
        Path tsnDir = mr.getCurrentTestSuiteDirectory()
        Path tstDir = tsnDir.resolve(tst.format())
        then:
        ! Files.exists(tstDir)
    }

    def testClear_withArgOnlyTSuiteName() {
        setup:
        Path casedir = workdir_.resolve("testClear_withArgOnlyTSuiteName")
        if (!Files.exists(casedir)) {
            Files.createDirectories(casedir)
        }
        Helpers.copyDirectory(fixture_, casedir)
        MaterialRepository mr = MaterialRepositoryFactory.createInstance(casedir.resolve('Materials'))
        when:
        TSuiteName tsn = new TSuiteName("Test Suites/main/TS1")
        TSuiteTimestamp tst = TSuiteTimestamp.newInstance("20180530_130419")
        int count = mr.clear(tsn)        // HERE is difference
        then:
        count == 22
        when:
        List<TSuiteResultId> tsriList = mr.getTSuiteResultIdList(tsn)
        List<TSuiteResult> list = mr.getTSuiteResultList(tsriList)
        then:
        list.size() == 0
        when:
        Path tsnDir = mr.getBaseDir().resolve(tsn.getValue())
        then:
        ! Files.exists(tsnDir)
    }

}

