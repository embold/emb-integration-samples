## About Embold:

Embold is an AI-based software analytics platform that helps teams analyse and improve software quality. It analyses source code across 4 dimensions: code issues, design issues, metrics and duplication, and surfaces issues which impact stability, robustness, security, and maintainability. The Embold Score helps teams understand risk areas and prioritise the most important fixes.

## Embold for Visual Studio Code:
Embold VSCode extension uncovers potential code issues, vulnerabilities, metrics and hard-to-detect anti-patterns that make your code difficult to maintain and can lead to error-prone solutions. The extension currently supports C++

## How it works:
1.	Right click on any .c/.cpp/.h/.hpp file or folder containing .c/.cpp/.h/.hpp any of these files.
2.  Click on 'Analyze Using Embold'.

 ![image info](https://github.com/embold/emb-integration-samples/blob/master/vscode_extension/images/Analyse.png?raw=true)
 
## Analysis of output : 
•	After scan completion analysis results will show up on problems view.

 ![image info](https://github.com/embold/emb-integration-samples/blob/master/vscode_extension/images/Problems.png?raw=true)

•	Show Description option is available on quick fix. For metric violation – quick fix will redirect to documentation page. 

 ![image info](https://github.com/embold/emb-integration-samples/blob/master/vscode_extension/images/MV.png?raw=true)

 ![image info](https://github.com/embold/emb-integration-samples/blob/master/vscode_extension/images/MVWeb.png?raw=true)

•	For Design Issue – It will show new tab for insights with animated progress bar.

 ![image info](https://github.com/embold/emb-integration-samples/blob/master/vscode_extension/images/DI.png?raw=true)
 
 ![image info](https://github.com/embold/emb-integration-samples/blob/master/vscode_extension/images/DIWebView.png?raw=true)
 
•	For Code Issue – It will redirect to documentation page (https://rules.embold.io/).

 ![image info](https://github.com/embold/emb-integration-samples/blob/master/vscode_extension/images/CI.png?raw=true)

 ![image info](https://github.com/embold/emb-integration-samples/blob/master/vscode_extension/images/CIWeb.png?raw=true)
 
## Requirements: 
1. Supported Vscode version: ^1.62.0
2. Cppcheck version : 2.4.1

## Install Cppcheck manually -
### windows -
1. Go to 'https://sourceforge.net/projects/cppcheck/files/cppcheck/2.4/cppcheck-2.4.1-x64-Setup.msi/download'.
2. Open downloaded .msi file.
3. Install Cppcheck.
4. Check version on cmd - cppcheck --version.

### linux -
1. wget https://sourceforge.net/projects/cppcheck/files/cppcheck/2.4/cppcheck-2.4.tar.gz/download
2. untar cppcheck-2.4.tar.gz file.
3. cd cppcheck-2.4.
4. sudo make install
5. sudo cp cppcheck /usr/bin/cppcheck
6. cppcheck --version

Supported Rules: https://rules.embold.io/

## Feedback
Feel free to use [Embold Community](https://community.embold.io/) to give feedback, request features or to report a bug.

## LICENSE

https://docs.embold.io/end-user-license-agreement-eula-for-ide-plugins/

