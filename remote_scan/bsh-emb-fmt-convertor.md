# bsh-emb-fmt-convertor
Bosch to Embold code issues format convertor (for import into Embold via gamma_generic)

This tool converts the code issue data from Bosch format to Embold (gamma_generic) format, which can then be used together with a remote scan, so that all the issues are imported to Embold


NOTE: This tool is currently in a prototype mode, meant to show the proof-of-concept.

## Pre-requisites:
- Java 1.8
- This will work only with the remote-scanning approach (which means you would run gammascanner on a remote machine which will run the Embold scan and publish results to the Embold server)
- The source version scanned by other tools (e.g. Bauhaus/QA-C) should be the same as the one Embold will scan
- The relative paths in the Bosch input CSV format should be relative to the same baseDir as the one that's configured in Embold remote scan. Otherwise, issues will not be reported
    - Example:
        - If base directory used in QA-C is: C:\Work\MySource
        - And the file paths in Input CSV are relative to this base directory (e.g. rb_ca_CoreAssets\rb_cd_ComplexDrivers\rb_csem_CentralSensorMgt\rb_csev_RampUpVerification.c)
        - Then the baseDir to be configured in the Embold scan (in `repository-configuration.json`) should also be `C:\Work\MySource`, and all the relative file structure should be the same as the one that was used by QA-C/Bauhaus
    
## Steps to run the convertor and the Embold scan
- First, run the convertor tool on the input CSV file:
1. For .csv/.xml reports - 
   1. -i : input file path to .csv/.xml file
   2. -o : output file path to output .csv file
   3. -t : tool name QA-C / Bauhaus / Compiler warnings
   4. -g : Optional - Tag, MultiTag (seperated by ; ) ,By default tag will be tool name (-t)

       ```bat
       bin\emb-convertor -i c:\Users\Admin\Downloads\QAC_Bauhaus_sample_findings.csv -o c:\Users\Admin\source\repos\BoschImport\gamma_generic\embold_gamma_generic.csv -t QA-C 
       ```
  The -o option in the above tool specifies the output file, let's create it inside the source repo directory to be scanned (e.g. in the `gamma_generic` folder)
- If the above tool run was successful, you should see the output file `embold_gamma_generic.csv`
- Examine the contents of this file. It should have the format: `File,Line,Severity,Description,Rule`
- Run the Embold remote scan on your repo with `gamma_generic` module enabled:
  
    Enable the `gamma_generic` section in your `repository-configuration.json` like this:
    ```json
    {
      "name": "gamma_generic",
      "enabled": true,
      "config": {
        "rules": [],
        "options": [
            {
                "name": "reportDir",
                "value": "gamma_generic"
            }
        ]
      }
    }
    ```
    Set `enabled` to `true` and the reportDir `value` to `gamma_generic`
- Run the remote scan in the usual way:
    `scanboxwrapper\bin\gammascanner -c repository-configuration.json`
  If all the pre-requisites are met, the scan will now pull-in issues we just exported into `embold_gamma_generic.csv` and publish it to the server, which you can then find on the `Issue List` page


