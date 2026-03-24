# BrowserStack Code Quality for Visual Studio

BrowserStack Code Quality (BrowserStack-CQ) is an AI-based software analytics platform designed to empower development teams in analyzing and significantly improving software quality. It deeply analyzes your source code across four critical dimensions: code issues, design issues, metrics, and duplication. By doing so, it uncovers and surfaces issues that directly impact the stability, robustness, security, and maintainability of your codebase. The innovative BrowserStack-CQ Score provides an intuitive understanding of risk areas, enabling teams to efficiently prioritize and address the most impactful fixes.

## Prerequisites

To use the BrowserStack Code Quality Visual Studio extension, ensure you have the following installed:

1.  **Visual Studio 2022 or later**
2.  **Java version 1.8 or later**

**Note:** This extension is currently **only available for C# projects**.

## How to Use

Follow these simple steps to analyze your C# project:

1.  **Navigate to the Tools Menu:**
    Open your Visual Studio IDE. In the top menu bar, click on **"Tools"**.

2.  **Initiate Code Analysis:**
    From the "Tools" dropdown menu, click on **"Analyse Project using BrowserStack Code Quality"**. The analysis process will begin.
    ![Tools Menu - Analyse Project using BrowserStack Code Quality](https://github.com/embold/emb-integration-samples/blob/master/visual_studio_extension/Images/tools_view.png?raw=true)

3.  **Review Analysis Results:**
    Once the analysis is complete, the results will automatically populate the **Error List** window within Visual Studio. Here, you can review all identified code quality issues, warnings, and messages.
    ![Error List Window - Analysis Results](https://github.com/embold/emb-integration-samples/blob/master/visual_studio_extension/Images/Issue_view.png?raw=true)

    To check more info on code issues, right click on issue from errorlist and select "Show Error Help".
    ![Error Help](https://github.com/embold/emb-integration-samples/blob/master/visual_studio_extension/Images/show_Error_help.png?raw=true)

    It will open issues page for more information.
    ![Embold rule page](https://github.com/embold/emb-integration-samples/blob/master/visual_studio_extension/Images/issues_page.png?raw=true)
## License

This plugin is governed by the BrowserStack Code Quality End User License Agreement (EULA). Please review the full terms and conditions at the following link:

[End User License Agreement (EULA) for IDE Plugins](https://docs.embold.io/end-user-license-agreement-eula-for-ide-plugins/)
