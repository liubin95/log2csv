# log2csv

![Build](https://github.com/liubin95/log2csv/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)

## 模板待办事项列表
- [x] 创建一个新的 [IntelliJ Platform Plugin Template][template] 项目。
- [ ] 熟悉 [模板文档][template]。
- [x] 调整 [pluginGroup](./gradle.properties) 和 [pluginName](./gradle.properties)，以及 [id](./src/main/resources/META-INF/plugin.xml) 和 [sources package](./src/main/kotlin)。
- [ ] 调整 `README` 中的插件描述（参见 [提示][docs:plugin-description]）
- [ ] 查看 [法律协议](https://plugins.jetbrains.com/docs/marketplace/legal-agreements.html?from=IJPluginTemplate)。
- [ ] 首次 [手动发布插件](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate)。
- [ ] 在上面的 README 徽章中设置 `MARKETPLACE_ID`。一旦插件发布到 JetBrains Marketplace 就可以获取它。
- [ ] 设置 [Plugin Signing](https://plugins.jetbrains.com/docs/intellij/plugin-signing.html?from=IJPluginTemplate) 相关的 [secrets](https://github.com/JetBrains/intellij-platform-plugin-template#environment-variables)。
- [ ] 设置 [Deployment Token](https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html?from=IJPluginTemplate)。
- [ ] 点击 [IntelliJ Platform Plugin Template][template] 顶部的 <kbd>Watch</kbd> 按钮，以便在包含新功能和修复的版本发布时收到通知。

<!-- Plugin description -->
这个优秀的 IntelliJ Platform 插件将成为实现您精妙想法的载体。

这个特定部分是 [plugin.xml](/src/main/resources/META-INF/plugin.xml) 文件的源代码，将在构建过程中由 [Gradle](/build.gradle.kts) 提取。

为保持所有功能正常运行，请不要删除 `<!-- ... -->` 部分。
<!-- Plugin description end -->

## 安装

- 使用 IDE 内置插件系统：

<kbd>设置/首选项</kbd> > <kbd>插件</kbd> > <kbd>Marketplace</kbd> > <kbd>搜索 "log2csv"</kbd> >
<kbd>安装</kbd>

- 使用 JetBrains Marketplace：

访问 [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID) 并点击 <kbd>Install to ...</kbd> 按钮安装（如果您的 IDE 正在运行）。

您也可以从 JetBrains Marketplace 下载 [最新版本](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID/versions) 并手动安装：
<kbd>设置/首选项</kbd> > <kbd>插件</kbd> > <kbd>⚙️</kbd> > <kbd>从磁盘安装插件...</kbd>

- 手动安装：

下载 [最新版本](https://github.com/liubin95/log2csv/releases/latest) 并手动安装：
<kbd>设置/首选项</kbd> > <kbd>插件</kbd> > <kbd>⚙️</kbd> > <kbd>从磁盘安装插件...</kbd>


---
插件基于 [IntelliJ Platform Plugin Template][template]。

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
