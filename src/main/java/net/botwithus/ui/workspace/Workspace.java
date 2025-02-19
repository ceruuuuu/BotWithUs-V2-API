package net.botwithus.ui.workspace;

import net.botwithus.logging.ConsoleHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static java.util.logging.Level.*;

public class Workspace {

    private final Logger log = Logger.getLogger(Workspace.class.getName());

    private boolean isScriptWindowOpen = false;

    private boolean isSettingsOpen = false;

    private boolean isConsoleOpen = false;

    private boolean isConsoleScrollToBottom = true;

    private final ConsoleHandler console;

    private String name;

    private String editingName;

    private String uuid;

    private Properties properties;

    private boolean isDirty;

    private final List<WorkspaceExtension> extensions;

    public Workspace() {
        this.name = "Default";
        this.uuid = UUID.randomUUID().toString();
        this.properties = new Properties();
        this.extensions = new ArrayList<>();
        this.editingName = this.name;
        this.isDirty = false;
        this.console = new ConsoleHandler();
        this.log.addHandler(console);
    }

    public boolean isScriptWindowOpen() {
        return isScriptWindowOpen;
    }

    public void setScriptWindowOpen(boolean scriptWindowOpen) {
        if(isScriptWindowOpen != scriptWindowOpen) {
            isDirty = true;
        }
        isScriptWindowOpen = scriptWindowOpen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(!this.name.equals(name)) {
            isDirty = true;
        }
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public Properties getProperties() {
        return properties;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public boolean isSettingsOpen() {
        return isSettingsOpen;
    }

    public void setSettingsOpen(boolean settingsOpen) {
        isSettingsOpen = settingsOpen;
    }

    public List<WorkspaceExtension> getExtensions() {
        return extensions;
    }

    public boolean hasExtension(Class<? extends WorkspaceExtension> type) {
        return extensions.stream().anyMatch(type::isInstance);
    }

    public void save() {
        properties.setProperty("name", name);
        properties.setProperty("uuid", uuid);
        properties.setProperty("scriptWindowOpen", String.valueOf(isScriptWindowOpen));
        properties.setProperty("consoleOpen", String.valueOf(isConsoleOpen));
        properties.setProperty("consoleScrollToBottom", String.valueOf(isConsoleScrollToBottom));
        for (WorkspaceExtension ext : extensions) {
            ext.onSave(this, properties);
        }

        isDirty = false;
    }

    public void load(Properties properties) {
        name = properties.getProperty("name");
        isScriptWindowOpen = Boolean.parseBoolean(properties.getProperty("scriptWindowOpen"));
        uuid = properties.getProperty("uuid");
        isConsoleOpen = Boolean.parseBoolean(properties.getProperty("consoleOpen"));
        isConsoleScrollToBottom = Boolean.parseBoolean(properties.getProperty("consoleScrollToBottom"));
        this.properties = new Properties(properties);

        for (WorkspaceExtension ext : extensions) {
            ext.onLoad(this, this.properties);
        }
    }

    public Logger getLogger() {
        return log;
    }

    public void drawSettings() {
        if (isSettingsOpen) {
            if (ImGui.begin("Settings", 0)) {
                ImGui.text("UUID: " + uuid);
                editingName = ImGui.inputTextWithHint("", "Enter name", this.editingName, 0);
                if (ImGui.button("Save", 0, 0)) {
                    setName(editingName);
                }
            }
            ImGui.end();
        }
        if(isConsoleOpen) {
            if(ImGui.begin("Console", 0)) {
                if(ImGui.button("Clear", 0, 0)) {
                    console.close();
                }
                ImGui.sameLine(0, 5);
                isConsoleScrollToBottom = ImGui.checkbox("Scroll to Bottom", isConsoleScrollToBottom);
                ImGui.separator();
                ImGui.beginChild("console", 0f, 0f, false,0);
                for (LogRecord record : console) {
                    Level level = record.getLevel();
                    String message = console.getFormatter().format(record);
                    if(level == INFO) {
                        ImGui.text(message);
                    } else if(level == WARNING) {
                        ImGui.textColored(message, 1, 1, 0, 1);
                    } else if(level == SEVERE) {
                        ImGui.textColored(message, 1, 0, 0, 1);
                    } else {
                        ImGui.text(message);
                    }
                    //tooltip
                }
                ImGui.endChild();
            }
            ImGui.end();
        }
    }

    public void setConsoleOpen(boolean showConsole) {
        isConsoleOpen = showConsole;
    }

    public boolean isConsoleOpen() {
        return isConsoleOpen;
    }

    public ConsoleHandler getConsole() {
        return console;
    }
}
