package seedu.letsgethired;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.letsgethired.commons.core.Config;
import seedu.letsgethired.commons.core.LogsCenter;
import seedu.letsgethired.commons.core.Version;
import seedu.letsgethired.commons.exceptions.DataLoadingException;
import seedu.letsgethired.commons.util.ConfigUtil;
import seedu.letsgethired.commons.util.StringUtil;
import seedu.letsgethired.logic.Logic;
import seedu.letsgethired.logic.LogicManager;
import seedu.letsgethired.model.InternTracker;
import seedu.letsgethired.model.Model;
import seedu.letsgethired.model.ModelManager;
import seedu.letsgethired.model.ReadOnlyInternTracker;
import seedu.letsgethired.model.ReadOnlyUserPrefs;
import seedu.letsgethired.model.UserPrefs;
import seedu.letsgethired.model.util.SampleDataUtil;
import seedu.letsgethired.storage.InternTrackerStorage;
import seedu.letsgethired.storage.JsonInternTrackerStorage;
import seedu.letsgethired.storage.JsonUserPrefsStorage;
import seedu.letsgethired.storage.Storage;
import seedu.letsgethired.storage.StorageManager;
import seedu.letsgethired.storage.UserPrefsStorage;
import seedu.letsgethired.ui.Ui;
import seedu.letsgethired.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final Version VERSION = new Version(1, 3, 2, true);

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    protected Config config;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing LetsGetHired ]===========================");
        super.init();

        AppParameters appParameters = AppParameters.parse(getParameters());
        config = initConfig(appParameters.getConfigPath());
        initLogging(config);

        UserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(config.getUserPrefsFilePath());
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        InternTrackerStorage dataStorage = new JsonInternTrackerStorage(userPrefs.getInternTrackerFilePath());
        storage = new StorageManager(dataStorage, userPrefsStorage);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic);
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s intern tracker and {@code userPrefs}. <br>
     * The data from the sample intern tracker will be used instead if {@code storage}'s intern tracker is not found,
     * or an empty intern tracker will be used instead if errors occur when reading {@code storage}'s intern tracker.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        logger.info("Using data file : " + storage.getInternTrackerFilePath());

        Optional<ReadOnlyInternTracker> dataOptional;
        ReadOnlyInternTracker initialData;
        try {
            dataOptional = storage.readInternTracker();
            if (!dataOptional.isPresent()) {
                logger.info("Creating a new data file " + storage.getInternTrackerFilePath()
                        + " populated with a sample InternTracker.");
            }
            initialData = dataOptional.orElseGet(SampleDataUtil::getSampleInternTracker);
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getInternTrackerFilePath() + " could not be loaded."
                    + " Will be starting with an empty InternTracker.");
            initialData = new InternTracker();
        }

        return new ModelManager(initialData, userPrefs);
    }

    private void initLogging(Config config) {
        LogsCenter.init(config);
    }

    /**
     * Returns a {@code Config} using the file at {@code configFilePath}. <br>
     * The default file path {@code Config#DEFAULT_CONFIG_FILE} will be used instead
     * if {@code configFilePath} is null.
     */
    protected Config initConfig(Path configFilePath) {
        Config initializedConfig;
        Path configFilePathUsed;

        configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

        if (configFilePath != null) {
            logger.info("Custom Config file specified " + configFilePath);
            configFilePathUsed = configFilePath;
        }

        logger.info("Using config file : " + configFilePathUsed);

        try {
            Optional<Config> configOptional = ConfigUtil.readConfig(configFilePathUsed);
            if (!configOptional.isPresent()) {
                logger.info("Creating new config file " + configFilePathUsed);
            }
            initializedConfig = configOptional.orElse(new Config());
        } catch (DataLoadingException e) {
            logger.warning("Config file at " + configFilePathUsed + " could not be loaded."
                    + " Using default config properties.");
            initializedConfig = new Config();
        }

        //Update config file in case it was missing to begin with or there are new/unused fields
        try {
            ConfigUtil.saveConfig(initializedConfig, configFilePathUsed);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }
        return initializedConfig;
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(UserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (!prefsOptional.isPresent()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting LetsGetHired " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping Intern Tracker ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
