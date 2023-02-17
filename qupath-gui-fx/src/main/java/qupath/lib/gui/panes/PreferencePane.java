/*-
 * #%L
 * This file is part of QuPath.
 * %%
 * Copyright (C) 2014 - 2016 The Queen's University of Belfast, Northern Ireland
 * Contact: IP Management (ipmanagement@qub.ac.uk)
 * Copyright (C) 2018 - 2022 QuPath developers, The University of Edinburgh
 * %%
 * QuPath is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * QuPath is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with QuPath.  If not, see <https://www.gnu.org/licenses/>.
 * #L%
 */

package qupath.lib.gui.panes;

import java.io.File;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.control.PropertySheet.Mode;
import org.controlsfx.control.SearchableComboBox;
import org.controlsfx.property.editor.AbstractPropertyEditor;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;
import org.controlsfx.property.editor.PropertyEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import javafx.util.StringConverter;
import qupath.lib.gui.QuPathResources;
import qupath.lib.gui.dialogs.Dialogs;
import qupath.lib.gui.logging.LogManager;
import qupath.lib.gui.logging.LogManager.LogLevel;
import qupath.lib.gui.prefs.PathPrefs;
import qupath.lib.gui.prefs.PathPrefs.AutoUpdateType;
import qupath.lib.gui.prefs.PathPrefs.DetectionTreeDisplayModes;
import qupath.lib.gui.prefs.PathPrefs.FontSize;
import qupath.lib.gui.prefs.PathPrefs.ImageTypeSetting;
import qupath.lib.gui.tools.ColorToolsFX;
import qupath.lib.gui.tools.CommandFinderTools;
import qupath.lib.gui.tools.CommandFinderTools.CommandBarDisplay;
import qupath.lib.gui.tools.GuiTools;
import qupath.lib.gui.tools.LocaleListener;
import qupath.lib.gui.prefs.QuPathStyleManager;
import qupath.lib.gui.prefs.QuPathStyleManager.StyleOption;

/**
 * Basic preference panel, giving a means to modify some of the properties within PathPrefs.
 * 
 * @author Pete Bankhead
 *
 */
public class PreferencePane {

	private static final Logger logger = LoggerFactory.getLogger(PreferencePane.class);

	private PropertySheet propSheet = new PropertySheet();
	
	private static LocaleManager localeManager = new LocaleManager();
	
	@SuppressWarnings("javadoc")
	public PreferencePane() {
		setupPanel();
	}
		
	@PrefCategory("Prefs.Appearance")
	static class AppearancePreferences {
		
		// TODO: Figure out how to pass available options
		@Pref(value = "Prefs.Appearance.theme", type = StyleOption.class)
		public final ObjectProperty<StyleOption> theme = QuPathStyleManager.selectedStyleProperty();
		
		@Pref(value = "Prefs.Appearance.font", type = QuPathStyleManager.Fonts.class)
		public final ObjectProperty<QuPathStyleManager.Fonts> autoUpdate = QuPathStyleManager.fontProperty();
		
	}
	

	@PrefCategory("Prefs.General")
	static class GeneralPreferences {
				
		@BooleanPref("Prefs.General.showStartupMessage")
		public final BooleanProperty startupMessage = PathPrefs.showStartupMessageProperty();

		@Pref(value = "Prefs.General.checkForUpdates", type = AutoUpdateType.class)
		public final ObjectProperty<AutoUpdateType> autoUpdate = PathPrefs.autoUpdateCheckProperty();

		@BooleanPref("Prefs.General.systemMenubar")
		public final BooleanProperty systemMenubar = PathPrefs.useSystemMenubarProperty();
		
		@DoublePref("Prefs.General.tileCache")
		public final DoubleProperty maxMemoryGB = PathPrefs.hasJavaPreferences() ? createMaxMemoryProperty() : null;

		@DoublePref("Prefs.General.tileCache")
		public final DoubleProperty tileCache = PathPrefs.tileCachePercentageProperty();

		@BooleanPref("Prefs.General.showImageNameInTitle")
		public final BooleanProperty showImageNameInTitle = PathPrefs.showImageNameInTitleProperty();

		@BooleanPref("Prefs.General.maskImageNames")
		public final BooleanProperty maskImageNames = PathPrefs.maskImageNamesProperty();
		
		@BooleanPref("Prefs.General.logFiles")
		public final BooleanProperty createLogFiles = PathPrefs.doCreateLogFilesProperty();

		@Pref(value = "Prefs.General.logLevel", type = LogLevel.class)
		public final ObjectProperty<LogLevel> logLevel = LogManager.rootLogLevelProperty();

		@IntegerPref("Prefs.General.logLevel")
		public final IntegerProperty numThreads = PathPrefs.numCommandThreadsProperty();

		@Pref(value = "Prefs.General.imageType", type = ImageTypeSetting.class)
		public final ObjectProperty<ImageTypeSetting> setImageType = PathPrefs.imageTypeSettingProperty();

		@Pref(value = "Prefs.General.commandBar", type = CommandBarDisplay.class)
		public final ObjectProperty<CommandBarDisplay> commandBarDisplay = CommandFinderTools.commandBarDisplayProperty();
		
		@BooleanPref("Prefs.General.showExperimental")
		public final BooleanProperty showExperimentalCommands = PathPrefs.showExperimentalOptionsProperty();

		@BooleanPref("Prefs.General.showTMA")
		public final BooleanProperty showTMACommands = PathPrefs.showTMAOptionsProperty();

		@BooleanPref("Prefs.General.showExperimental")
		public final BooleanProperty showDeprecatedCommands = PathPrefs.showLegacyOptionsProperty();
		
		@Pref(value = "Prefs.General.showDeprecated", type = DetectionTreeDisplayModes.class)
		public final ObjectProperty<DetectionTreeDisplayModes> hierarchyDisplayMode = PathPrefs.detectionTreeDisplayModeProperty();
		
		
		private DoubleProperty createMaxMemoryProperty() {
			long maxMemoryMB = Runtime.getRuntime().maxMemory() / 1024 / 1024;
			DoubleProperty propMemoryGB = new SimpleDoubleProperty(maxMemoryMB / 1024.0);
			propMemoryGB.addListener((v, o, n) -> {
				int requestedMemoryMB = (int)Math.round(propMemoryGB.get() * 1024.0);
				if (requestedMemoryMB > 1024) {
					boolean success = false;
					try {
						PathPrefs.maxMemoryMBProperty().set(requestedMemoryMB);		
						success = requestedMemoryMB == PathPrefs.maxMemoryMBProperty().get();
					} catch (Exception e) {
						logger.error(e.getLocalizedMessage(), e);
					}
					if (success) {
						Dialogs.showInfoNotification(QuPathResources.getString("Prefs.General.maxMemory"),
								"Setting max memory to " + requestedMemoryMB + " MB - you'll need to restart QuPath for this to take effect"
								);
					} else {
						Dialogs.showErrorMessage("Set max memory",
								"Unable to set max memory - sorry!\n"
								+ "Check the FAQs on ReadTheDocs for details how to set the "
								+ "memory limit by editing QuPath's config file."
								);						
					}
				}
			});	
			return propMemoryGB;
		}
				
	}
	
	
	private void parseAndAddItems(Object object) {
		var items = parseItems(object);
		propSheet.getItems().addAll(items);		
	}
	
	
	@PrefCategory("Prefs.Locale")
	static class LocalePreferences {
		
		@LocalePref("Prefs.Locale.default")
		public final ObjectProperty<Locale> localeDefault = PathPrefs.defaultLocaleProperty();

		@LocalePref("Prefs.Locale.display")
		public final ObjectProperty<Locale> localeDisplay = PathPrefs.defaultLocaleDisplayProperty();

		@LocalePref("Prefs.Locale.format")
		public final ObjectProperty<Locale> localeFormat = PathPrefs.defaultLocaleFormatProperty();

	}
	
	
	@PrefCategory("Prefs.General")
	static class InputOutputPreferences {
		
		@IntegerPref("Prefs.InputOutput.minPyramidDimension")
		public final IntegerProperty minimumPyramidDimension = PathPrefs.minPyramidDimensionProperty();
		
		@DoublePref("Prefs.InputOutput.tmaExportDownsample")
		public final DoubleProperty tmaExportDownsample = PathPrefs.tmaExportDownsampleProperty();
		
	}
	
	
	@PrefCategory("Prefs.Viewer")
	static class ViewerPreferences {
		
		@ColorPref("Prefs.Viewer.backgroundColor")
		public final IntegerProperty backgroundColor = PathPrefs.viewerBackgroundColorProperty();

		@BooleanPref("Prefs.Viewer.alwaysPaintSelected")
		public final BooleanProperty alwaysPaintSelected = PathPrefs.alwaysPaintSelectedObjectsProperty();

		@BooleanPref("Prefs.Viewer.keepDisplaySettings")
		public final BooleanProperty keepDisplaySettings = PathPrefs.keepDisplaySettingsProperty();

		@BooleanPref("Prefs.Viewer.interpolateBilinear")
		public final BooleanProperty interpolateBilinear = PathPrefs.viewerInterpolateBilinearProperty();

		@DoublePref("Prefs.Viewer.autoSaturationPercent")
		public final DoubleProperty autoSaturationPercent = PathPrefs.autoBrightnessContrastSaturationPercentProperty();

		@BooleanPref("Prefs.Viewer.invertZSlider")
		public final BooleanProperty invertZSlider = PathPrefs.invertZSliderProperty();

		@IntegerPref("Prefs.Viewer.scrollSpeed")
		public final IntegerProperty scrollSpeed = PathPrefs.scrollSpeedProperty();

		@IntegerPref("Prefs.Viewer.navigationSpeed")
		public final IntegerProperty navigationSpeed = PathPrefs.navigationSpeedProperty();
		

		@BooleanPref("Prefs.Viewer.navigationAcceleration")
		public final BooleanProperty navigationAcceleration = PathPrefs.navigationAccelerationProperty();

		@BooleanPref("Prefs.Viewer.skipMissingCores")
		public final BooleanProperty skipMissingCores = PathPrefs.skipMissingCoresProperty();

		@BooleanPref("Prefs.Viewer.iseScrollGestures")
		public final BooleanProperty iseScrollGestures = PathPrefs.useScrollGesturesProperty();

		@BooleanPref("Prefs.Viewer.invertScrolling")
		public final BooleanProperty invertScrolling = PathPrefs.invertScrollingProperty();

		@BooleanPref("Prefs.Viewer.useZoomGestures")
		public final BooleanProperty useZoomGestures = PathPrefs.useZoomGesturesProperty();

		@BooleanPref("Prefs.Viewer.useRotateGestures")
		public final BooleanProperty useRotateGestures = PathPrefs.useRotateGesturesProperty();

		@BooleanPref("Prefs.Viewer.enableFreehand")
		public final BooleanProperty enableFreehand = PathPrefs.enableFreehandToolsProperty();

		@BooleanPref("Prefs.Viewer.doubleClickToZoom")
		public final BooleanProperty doubleClickToZoom = PathPrefs.doubleClickToZoomProperty();
		
		
		@Pref(value = "Prefs.Viewer.scalebarFontSize", type = FontSize.class)		
		public final ObjectProperty<FontSize> scalebarFontSize = PathPrefs.scalebarFontSizeProperty();

		@Pref(value = "Prefs.Viewer.scalebarFontWeight", type = FontWeight.class)		
		public final ObjectProperty<FontWeight> scalebarFontWeight = PathPrefs.scalebarFontWeightProperty();

		@DoublePref("Prefs.Viewer.scalebarLineWidth")
		public final DoubleProperty scalebarLineWidth = PathPrefs.scalebarLineWidthProperty();

		@Pref(value = "Prefs.Viewer.locationFontSize", type = FontSize.class)		
		public final ObjectProperty<FontSize> locationFontSize = PathPrefs.locationFontSizeProperty();

		@BooleanPref("Prefs.Viewer.calibratedLocationString")
		public final BooleanProperty calibratedLocationString = PathPrefs.useCalibratedLocationStringProperty();

		@DoublePref("Prefs.Viewer.gridSpacingX")
		public final DoubleProperty gridSpacingX = PathPrefs.gridSpacingXProperty();

		@DoublePref("Prefs.Viewer.gridSpacingY")
		public final DoubleProperty gridSpacingY = PathPrefs.gridSpacingYProperty();

		@BooleanPref("Prefs.Viewer.gridScaleMicrons")
		public final BooleanProperty gridScaleMicrons = PathPrefs.gridScaleMicronsProperty();

	}
	
	
	@PrefCategory("Prefs.Extensions")
	static class ExtensionPreferences {
		
		@DirectoryPref("Prefs.Extensions.userPath")
		public final Property<String> scriptsPath = PathPrefs.userPathProperty();

	}
	
	
	@PrefCategory("Prefs.Measurements")
	static class MeasurementPreferences {
		
		@BooleanPref("Prefs.Measurements.thumbnails")
		public final BooleanProperty showMeasurementTableThumbnails = PathPrefs.showMeasurementTableThumbnailsProperty();

		@BooleanPref("Prefs.Measurements.ids")
		public final BooleanProperty showMeasurementTableObjectIDs = PathPrefs.showMeasurementTableObjectIDsProperty();

	}
	
	
	@PrefCategory("Prefs.Scripting")
	static class ScriptingPreferences {
		
		@DirectoryPref("Prefs.Scripting.scriptsPath")
		public final StringProperty scriptsPath = PathPrefs.scriptsPathProperty();

	}

	
	@PrefCategory("Prefs.Drawing")
	static class DrawingPreferences {
		
		@BooleanPref("Prefs.Drawing.returnToMove")
		public final BooleanProperty returnToMove = PathPrefs.returnToMoveModeProperty();

		@BooleanPref("Prefs.Drawing.pixelSnapping")
		public final BooleanProperty pixelSnapping = PathPrefs.usePixelSnappingProperty();

		@BooleanPref("Prefs.Drawing.clipROIsForHierarchy")
		public final BooleanProperty clipROIsForHierarchy = PathPrefs.clipROIsForHierarchyProperty();

		@IntegerPref("Prefs.Drawing.brushDiameter")
		public final IntegerProperty brushDiameter = PathPrefs.brushDiameterProperty();		
		
		@BooleanPref("Prefs.Drawing.tileBrush")
		public final BooleanProperty tileBrush = PathPrefs.useTileBrushProperty();

		@BooleanPref("Prefs.Drawing.brushScaleByMag")
		public final BooleanProperty brushScaleByMag = PathPrefs.brushScaleByMagProperty();

		@BooleanPref("Prefs.Drawing.useMultipoint")
		public final BooleanProperty useMultipoint = PathPrefs.multipointToolProperty();
		
		@IntegerPref("Prefs.Drawing.pointRadius")
		public final IntegerProperty pointRadius = PathPrefs.pointRadiusProperty();
		
	}
	
	
	@PrefCategory("Prefs.Objects")
	static class ObjectPreferences {
		
		@IntegerPref("Prefs.Objects.clipboard")
		public final IntegerProperty maxClipboardObjects = PathPrefs.maxObjectsToClipboardProperty();

		@DoublePref("Prefs.Objects.annotationLineThickness")
		public final DoubleProperty annotationStrokeThickness = PathPrefs.annotationStrokeThicknessProperty();

		@DoublePref("Prefs.Objects.detectionLineThickness")
		public final DoubleProperty detectonStrokeThickness = PathPrefs.detectionStrokeThicknessProperty();

		@BooleanPref("Prefs.Objects.useSelectedColor")
		public final BooleanProperty useSelectedColor = PathPrefs.useSelectedColorProperty();

		@ColorPref("Prefs.Objects.selectedColor")
		public final IntegerProperty selectedColor = PathPrefs.colorSelectedObjectProperty();

		@ColorPref("Prefs.Objects.defaultColor")
		public final IntegerProperty defaultColor = PathPrefs.colorDefaultObjectsProperty();

		@ColorPref("Prefs.Objects.tmaCoreColor")
		public final IntegerProperty tmaColor = PathPrefs.colorTMAProperty();

		@ColorPref("Prefs.Objects.tmaCoreMissingColor")
		public final IntegerProperty tmaMissingColor = PathPrefs.colorTMAMissingProperty();

	}
	

	private void setupPanel() {
		//		propSheet.setMode(Mode.CATEGORY);
		propSheet.setMode(Mode.CATEGORY);
		propSheet.setPropertyEditorFactory(new PropertyEditorFactory());

		parseAndAddItems(new AppearancePreferences());
		parseAndAddItems(new GeneralPreferences());
		parseAndAddItems(new LocalePreferences());
		parseAndAddItems(new InputOutputPreferences());

		parseAndAddItems(new ViewerPreferences());
		parseAndAddItems(new ExtensionPreferences());
		parseAndAddItems(new MeasurementPreferences());
		parseAndAddItems(new ScriptingPreferences());
		
		parseAndAddItems(new DrawingPreferences());
		parseAndAddItems(new ObjectPreferences());
	}

	/**
	 * Get the property sheet for this {@link PreferencePane}.
	 * This is a {@link Node} that may be added to a scene for display.
	 * @return
	 */
	public PropertySheet getPropertySheet() {
		return propSheet;
	}


	
	/**
	 * Add a new preference based on a specified Property.
	 * 
	 * @param prop
	 * @param cls
	 * @param name
	 * @param category
	 * @param description
	 */
	public <T> void addPropertyPreference(final Property<T> prop, final Class<? extends T> cls, final String name, final String category, final String description) {
		PropertySheet.Item item = new DefaultPropertyItem<>(prop, cls)
				.name(name)
				.category(category)
				.description(description);
		propSheet.getItems().add(item);
	}
	
	
	/**
	 * Add a new color preference based on a specified IntegerProperty (storing a packed RGBA value).
	 * 
	 * @param prop
	 * @param name
	 * @param category
	 * @param description
	 */
	public void addColorPropertyPreference(final IntegerProperty prop, final String name, final String category, final String description) {
		PropertySheet.Item item = new ColorPropertyItem(prop)
				.name(name)
				.category(category)
				.description(description);
		propSheet.getItems().add(item);
	}
	
	
	/**
	 * Add a new directory preference based on a specified StrongProperty.
	 * 
	 * @param prop
	 * @param name
	 * @param category
	 * @param description
	 */
	@Deprecated
	public void addDirectoryPropertyPreference(final Property<String> prop, final String name, final String category, final String description) {
		PropertySheet.Item item = new DirectoryPropertyItem(prop)
				.name(name)
				.category(category)
				.description(description);
		propSheet.getItems().add(item);
	}
	
	
	/**
	 * Add a new choice preference, to select from a list of possibilities.
	 * 
	 * @param prop
	 * @param choices
	 * @param cls
	 * @param name
	 * @param category
	 * @param description
	 */
	@Deprecated
	public <T> void addChoicePropertyPreference(final Property<T> prop, final ObservableList<T> choices, final Class<? extends T> cls, final String name, final String category, final String description) {
		addChoicePropertyPreference(prop, choices, cls, name, category, description, false);
	}
	

	/**
	 * Add a new choice preference, to select from an optionally searchable list of possibilities.
	 * 
	 * @param prop
	 * @param choices
	 * @param cls
	 * @param name
	 * @param category
	 * @param description
	 * @param makeSearchable make the choice item's editor searchable (useful for long lists)
	 */
	@Deprecated
	public <T> void addChoicePropertyPreference(final Property<T> prop, final ObservableList<T> choices, final Class<? extends T> cls, 
			final String name, final String category, final String description, boolean makeSearchable) {
		PropertySheet.Item item = new ChoicePropertyItem<>(prop, choices, cls, makeSearchable)
				.name(name)
				.category(category)
				.description(description);
		propSheet.getItems().add(item);
	}
	
	
//	public <T> void addChoicePropertyPreference(final Property<T> prop, final ObservableList<T> choices, final Class<? extends T> cls, 
//			final String key) {
//		PropertySheet.Item item = new ChoicePropertyItem<>(prop, choices, cls, false)
//				.resource(key);
//		propSheet.getItems().add(item);
//	}
//	
//	public <T> void addSearchableChoicePropertyPreference(final Property<T> prop, final ObservableList<T> choices, final Class<? extends T> cls, final String key) {
//		PropertySheet.Item item = new ChoicePropertyItem<>(prop, choices, cls, true)
//				.resource(key);
//		propSheet.getItems().add(item);
//	}
	
	
	/**
	 * Request that all the property editors are regenerated.
	 * This is useful if the Locale has changed, and so the text may need to be updated.
	 */
	public void refreshAllEditors() {
		// Force all editors to be recreated
		// This is useful when the locale has been changed
		var items = new ArrayList<>(propSheet.getItems());
		propSheet.getItems().clear();
		propSheet.getItems().addAll(items);
	}

	
	/**
	 * Create a default {@link Item} for a generic property.
	 * @param <T> type of the property
	 * @param property the property
	 * @param cls the property type
	 * @return a new {@link PropertyItem}
	 */
	public static <T> PropertyItem createPropertySheetItem(Property<T> property, Class<? extends T> cls) {
		return new DefaultPropertyItem<>(property, cls);
	}
	
	
	/**
	 * Base implementation of {@link Item}.
	 */
	public abstract static class PropertyItem implements PropertySheet.Item {

		private StringProperty name = new SimpleStringProperty();
		private StringProperty category = new SimpleStringProperty();
		private StringProperty description = new SimpleStringProperty();

		/**
		 * Support fluent interface to define a category.
		 * @param category
		 * @return
		 */
		public PropertyItem category(final String category) {
			this.category.set(category);
			return this;
		}

		/**
		 * Support fluent interface to set the description.
		 * @param description
		 * @return
		 */
		public PropertyItem description(final String description) {
			this.description.set(description);
			return this;
		}

		/**
		 * Support fluent interface to set the name.
		 * @param name
		 * @return
		 */
		public PropertyItem name(final String name) {
			this.name.set(name);
			return this;
		}
		
		public PropertyItem key(final String bundle, final String key) {
			LocaleListener.registerProperty(name, bundle, key);
			if (QuPathResources.hasString(bundle, key + ".description"))
				LocaleListener.registerProperty(description, bundle, key + ".description");			
			return this;
		}

		public PropertyItem categoryKey(final String bundle, final String key) {
			LocaleListener.registerProperty(category, bundle, key);			
			return this;
		}


		@Override
		public String getCategory() {
			return category.get();
		}

		@Override
		public String getName() {
			return name.get();
		}

		@Override
		public String getDescription() {
			return description.get();
		}

	}


	private static class DefaultPropertyItem<T> extends PropertyItem {

		private Property<T> prop;
		private Class<? extends T> cls;

		DefaultPropertyItem(final Property<T> prop, final Class<? extends T> cls) {
			this.prop = prop;
			this.cls = cls;
		}

		@Override
		public Class<?> getType() {
			return cls;
		}

		@Override
		public Object getValue() {
			return prop.getValue();
		}

		@SuppressWarnings("unchecked")
		@Override
		public void setValue(Object value) {
			prop.setValue((T)value);
		}

		@Override
		public Optional<ObservableValue<? extends Object>> getObservableValue() {
			return Optional.of(prop);
		}

	}


	/**
	 * Create a property item that handles directories based on String paths.
	 */
	private static class DirectoryPropertyItem extends PropertyItem {

		private Property<String> prop;
		private ObservableValue<File> fileValue;

		DirectoryPropertyItem(final Property<String> prop) {
			this.prop = prop;
			fileValue = Bindings.createObjectBinding(() -> prop.getValue() == null ? null : new File(prop.getValue()), prop);
		}

		@Override
		public Class<?> getType() {
			return File.class;
		}

		@Override
		public Object getValue() {
			return fileValue.getValue();
		}

		@Override
		public void setValue(Object value) {
			if (value instanceof String)
				prop.setValue((String)value);
			else if (value instanceof File)
				prop.setValue(((File)value).getAbsolutePath());
			else if (value == null)
				prop.setValue(null);
			else
				logger.error("Cannot set property {} with value {}", prop, value);
		}

		@Override
		public Optional<ObservableValue<? extends Object>> getObservableValue() {
			return Optional.of(fileValue);
		}

	}


	private static class ColorPropertyItem extends PropertyItem {

		private IntegerProperty prop;
		private ObservableValue<Color> value;

		ColorPropertyItem(final IntegerProperty prop) {
			this.prop = prop;
			this.value = Bindings.createObjectBinding(() -> ColorToolsFX.getCachedColor(prop.getValue()), prop);
		}

		@Override
		public Class<?> getType() {
			return Color.class;
		}

		@Override
		public Object getValue() {
			return value.getValue();
		}

		@Override
		public void setValue(Object value) {
			if (value instanceof Color)
				value = ColorToolsFX.getARGB((Color)value);
			if (value instanceof Integer)
				prop.setValue((Integer)value);
		}

		@Override
		public Optional<ObservableValue<? extends Object>> getObservableValue() {
			return Optional.of(value);
		}

	}
	
	
	private static class ChoicePropertyItem<T> extends DefaultPropertyItem<T> {

		private final ObservableList<T> choices;
		private final boolean makeSearchable;
		
		private ChoicePropertyItem(final Property<T> prop, final ObservableList<T> choices, final Class<? extends T> cls) {
			this(prop, choices, cls, false);
		}

		private ChoicePropertyItem(final Property<T> prop, final ObservableList<T> choices, final Class<? extends T> cls, boolean makeSearchable) {
			super(prop, cls);
			this.choices = choices;
			this.makeSearchable = makeSearchable;
		}
		
		public ObservableList<T> getChoices() {
			return choices;
		}
		
		public boolean makeSearchable() {
			return makeSearchable;
		}

	}



	/**
	 * Editor for selecting directory paths.
	 * 
	 * Appears as a text field that can be double-clicked to launch a directory chooser.
	 */
	private static class DirectoryEditor extends AbstractPropertyEditor<File, TextField> {

		private ObservableValue<File> value;

		private DirectoryEditor(Item property, TextField control) {
			super(property, control, true);
			control.setOnMouseClicked(e -> {
				if (e.getClickCount() > 1) {
					e.consume();
					File dirNew = Dialogs.getChooser(control.getScene().getWindow()).promptForDirectory(getValue());
					if (dirNew != null)
						setValue(dirNew);
				}
			});
			if (property.getDescription() != null) {
				var description = property.getDescription();
				var tooltip = new Tooltip(description);
				tooltip.setShowDuration(Duration.millis(10_000));
				control.setTooltip(tooltip);
			}
			
			// Bind to the text property
			if (property instanceof DirectoryPropertyItem) {
				control.textProperty().bindBidirectional(((DirectoryPropertyItem)property).prop);
			}
			value = Bindings.createObjectBinding(() -> {
				String text = control.getText();
				if (text == null || text.trim().isEmpty() || !new File(text).isDirectory())
					return null;
				else
					return new File(text);
				}, control.textProperty());
		}

		@Override
		public void setValue(File value) {
			getEditor().setText(value == null ? null : value.getAbsolutePath());
		}

		@Override
		protected ObservableValue<File> getObservableValue() {
			return value;
		}

	}
	
	/**
	 * Manage available locales, with consistent display and string conversion.
	 * This is needed to support presenting locales in a searchable combo box.
	 * <p>
	 * The price of this is that the language/locale names are always shown in English.
	 */
	private static class LocaleManager {
		
		private Map<String, Locale> localeMap = new TreeMap<>();
		private StringConverter<Locale> converter;
		
		private LocaleManager() {
			initializeLocaleMap();
			converter = new LocaleConverter();
		}
		
		private void initializeLocaleMap() {
			for (var locale : Locale.getAvailableLocales()) {
				if (!localeFilter(locale))
					continue;
				var name = locale.getDisplayName(Locale.US);
				localeMap.putIfAbsent(name, locale);
			}
		}
		
		private static boolean localeFilter(Locale locale) {
			if (locale == Locale.US)
				return true;
			return !locale.getLanguage().isBlank() && locale.getCountry().isEmpty() && locale != Locale.ENGLISH;
		}
		
		private static String getDisplayName(Locale locale) {
			// We use the English US display name, because we're guaranteed that Java supports it 
			// - and also it avoids needing to worry about non-unique names being generated 
			// in different locales, which could mess up the searchable combo box & string converter
			return locale.getDisplayName(Locale.US);
		}
		
		public ObservableList<Locale> createLocaleList() {
			return FXCollections.observableArrayList(localeMap.values());
		}
		
		public StringConverter<Locale> getStringConverter() {
			return converter;
		}
		
		class LocaleConverter extends StringConverter<Locale> {

			@Override
			public String toString(Locale locale) {
				if (locale == null)
					return "";
				return getDisplayName(locale);
			}

			@Override
			public Locale fromString(String string) {
				return localeMap.getOrDefault(string, null);
			}
			
		}
		
		
	}
	
	/**
	 * Editor for choosing from a longer list of items, aided by a searchable combo box.
	 * @param <T> 
	 */
	static class SearchableChoiceEditor<T> extends AbstractPropertyEditor<T, SearchableComboBox<T>> {

		public SearchableChoiceEditor(Item property, Collection<? extends T> choices) {
			this(property, FXCollections.observableArrayList(choices));
		}

		public SearchableChoiceEditor(Item property, ObservableList<T> choices) {
			super(property, new SearchableComboBox<T>());
			if (property.getType().equals(Locale.class))
				getEditor().setConverter((StringConverter<T>)localeManager.getStringConverter());
			getEditor().setItems(choices);
		}

		@Override
		public void setValue(T value) {
			// Only set the value if it's available as a choice
			if (getEditor().getItems().contains(value))
				getEditor().getSelectionModel().select(value);
		}

		@Override
		protected ObservableValue<T> getObservableValue() {
			return getEditor().getSelectionModel().selectedItemProperty();
		}
		
	}
	
	/**
	 * Editor for choosing from a combo box, which will use an observable list directly if it can 
	 * (which differs from ControlsFX's default behavior).
	 *
	 * @param <T>
	 */
	static class ChoiceEditor<T> extends AbstractPropertyEditor<T, ComboBox<T>> {

		public ChoiceEditor(Item property, Collection<? extends T> choices) {
			this(property, FXCollections.observableArrayList(choices));
		}

		public ChoiceEditor(Item property, ObservableList<T> choices) {
			super(property, new ComboBox<T>());
			getEditor().setItems(choices);
		}

		@Override
		public void setValue(T value) {
			// Only set the value if it's available as a choice
			if (getEditor().getItems().contains(value))
				getEditor().getSelectionModel().select(value);
		}

		@Override
		protected ObservableValue<T> getObservableValue() {
			return getEditor().getSelectionModel().selectedItemProperty();
		}
		
	}
	
	
	// We want to reformat the display of these to avoid using all uppercase
	private static Map<Class<?>, Function<?, String>> reformatTypes = Map.of(
			FontWeight.class, PreferencePane::simpleFormatter,
			LogLevel.class, PreferencePane::simpleFormatter
			);
	
	private static String simpleFormatter(Object obj) {
		var s = Objects.toString(obj);
		s = s.replaceAll("_", " ");
		if (Objects.equals(s, s.toUpperCase()))
			return s.substring(0, 1) + s.substring(1).toLowerCase();
		return s;
	}
	
	/**
	 * Extends {@link DefaultPropertyEditorFactory} to handle setting directories and creating choice editors.
	 */
	public static class PropertyEditorFactory extends DefaultPropertyEditorFactory {

		@SuppressWarnings("unchecked")
		@Override
		public PropertyEditor<?> call(Item item) {
			if (item.getType() == File.class) {
				return new DirectoryEditor(item, new TextField());
			}
			PropertyEditor<?> editor;
			if (item instanceof ChoicePropertyItem) {
				var choiceItem = ((ChoicePropertyItem<?>)item);
				if (choiceItem.makeSearchable()) {
					editor = new SearchableChoiceEditor<>(choiceItem, choiceItem.getChoices());
				} else
					// Use this rather than Editors because it wraps an existing ObservableList where available
					editor = new ChoiceEditor<>(choiceItem, choiceItem.getChoices());
//					editor = Editors.createChoiceEditor(item, choiceItem.getChoices());
			} else
				editor = super.call(item);
			if (reformatTypes.containsKey(item.getType()) && editor.getEditor() instanceof ComboBox) {
				@SuppressWarnings("rawtypes")
				var combo = (ComboBox)editor.getEditor();
				var formatter = reformatTypes.get(item.getType());
				combo.setCellFactory(obj -> GuiTools.createCustomListCell(formatter));
				combo.setButtonCell(GuiTools.createCustomListCell(formatter));
			}
			
			// Make it easier to reset default locale
			if (Locale.class.equals(item.getType())) {
				editor.getEditor().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
					if (e.getClickCount() == 2) {
						if (Dialogs.showConfirmDialog("Reset locale", "Reset locale to 'English (United States)'?")) {
							item.setValue(Locale.US);
						}
					}
				});
			}
			
			return editor;
		}
	}
	
	
	private static <T> PropertyItemBuilder<T> buildItem(Property<T> prop, final Class<? extends T> cls) {
		return new PropertyItemBuilder(prop, cls);
	}

	
	static enum PropertyType { GENERAL, DIRECTORY, COLOR, CHOICE, SEARCHABLE_CHOICE }
	
	
	static class PropertyItemBuilder<T> {
		
		private Property<T> property;
		private Class<? extends T> cls;
		
		private PropertyType propertyType = PropertyType.GENERAL;
		private ObservableList<T> choices;
		
		private String bundle;
		private String key;
		private String categoryKey;
		
		private PropertyItemBuilder(Property<T> prop, final Class<? extends T> cls) {
			this.property = prop;
			this.cls = cls;
		}
		
		public PropertyItemBuilder<T> key(String key) {
			this.key = key;
			return this;
		}

		public PropertyItemBuilder<T> category(String key) {
			this.categoryKey = key;
			return this;
		}
		
		public PropertyItemBuilder<T> color() {
			return propertyType(PropertyType.COLOR);
		}

		public PropertyItemBuilder<T> directory() {
			return propertyType(PropertyType.DIRECTORY);
		}

		public PropertyItemBuilder<T> propertyType(PropertyType type) {
			this.propertyType = type;
			return this;
		}
		
		public PropertyItemBuilder<T> choices(Collection<T> choices) {
			return choices(FXCollections.observableArrayList(choices));
		}
		
		public PropertyItemBuilder<T> choices(ObservableList<T> choices) {
			this.choices = choices;
			this.propertyType = PropertyType.CHOICE;
			return this;
		}
		
		public PropertyItemBuilder<T> bundle(String name) {
			this.bundle = name;
			return this;
		}

		public PropertyItem build() {
			PropertyItem item;
			switch (propertyType) {
			case DIRECTORY:
				item = new DirectoryPropertyItem((Property<String>)property);
				break;
			case COLOR:
				item = new ColorPropertyItem((IntegerProperty)property);
				break;
			case CHOICE:
				item = new ChoicePropertyItem<>(property, choices, cls, false);
				break;
			case SEARCHABLE_CHOICE:
				item = new ChoicePropertyItem<>(property, choices, cls, true);
				break;
			case GENERAL:
			default:
				item = new DefaultPropertyItem<>(property, cls);
				break;
			}
			if (key != null)
				item.key(bundle, key);
			if (categoryKey != null) {
				item.categoryKey(bundle, categoryKey);
			}
			return item;
		}
		
	}
	
	
	
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.FIELD})
	public @interface Pref {
		Class<?> type();
		String value();
	}

	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.FIELD})
	public @interface IntegerPref {
		String value();
	}
	
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.FIELD})
	public @interface DoublePref {
		String value();
	}
	
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.FIELD})
	public @interface BooleanPref {
		String value();
	}
	
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.FIELD})
	public @interface DirectoryPref {
		String value();
	}
	
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.FIELD})
	public @interface LocalePref {
		String value();
	}
	
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.FIELD})
	public @interface ColorPref {
		String value();
	}
	
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.FIELD})
	public @interface PrefCategory {
		String bundle() default "";
		String value();
	}

	
	public static List<PropertyItem> parseItems(Object obj) {
		
		var cls = obj instanceof Class<?> ? (Class<?>)obj : obj.getClass();
		List<PropertyItem> items = new ArrayList<>();
		
		String categoryBundle = null;
		String categoryKey = "Prefs.General";
		if (cls.isAnnotationPresent(PrefCategory.class)) {
			var annotation = cls.getAnnotation(PrefCategory.class);
			categoryBundle = annotation.bundle().isBlank() ? null : annotation.bundle();
			categoryKey = annotation.value();
		}
		
		for (var field : cls.getDeclaredFields()) {
			if (!field.canAccess(obj) || !Property.class.isAssignableFrom(field.getType()))
				continue;
			PropertyItem item = null;
			try {
				// Skip null fields
				if (field.get(obj) == null)
					continue;
				
				if (field.isAnnotationPresent(Pref.class)) {
					item = parseItem((Property)field.get(obj), field.getAnnotation(Pref.class));
				} else if (field.isAnnotationPresent(BooleanPref.class)) {
					item = parseItem((BooleanProperty)field.get(obj), field.getAnnotation(BooleanPref.class));
				} else if (field.isAnnotationPresent(IntegerPref.class)) {
					item = parseItem((IntegerProperty)field.get(obj), field.getAnnotation(IntegerPref.class));
				} else if (field.isAnnotationPresent(DoublePref.class)) {
					item = parseItem((DoubleProperty)field.get(obj), field.getAnnotation(DoublePref.class));
				} else if (field.isAnnotationPresent(LocalePref.class)) {
					item = parseItem((Property<Locale>)field.get(obj), field.getAnnotation(LocalePref.class));
				} else if (field.isAnnotationPresent(ColorPref.class)) {
					item = parseItem((Property<Integer>)field.get(obj), field.getAnnotation(ColorPref.class));
				}else if (field.isAnnotationPresent(DirectoryPref.class)) {
					item = parseItem((Property<String>)field.get(obj), field.getAnnotation(DirectoryPref.class));
				}
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
			if (item != null) {
				item.categoryKey(categoryBundle, categoryKey);
				items.add(item);
			}
		}
		
		return items;
		
	}
	
	private static PropertyItem parseItem(Property property, Pref annotation) {
		return buildItem(property, annotation.type())
				.key(annotation.value())
				.build();
	}
	
	private static PropertyItem parseItem(BooleanProperty property, BooleanPref annotation) {
		return buildItem(property, Boolean.class)
				.key(annotation.value())
				.build();
	}
	
	private static PropertyItem parseItem(IntegerProperty property, IntegerPref annotation) {
		return buildItem(property, Integer.class)
				.key(annotation.value())
				.build();
	}
	
	private static PropertyItem parseItem(DoubleProperty property, DoublePref annotation) {
		return buildItem(property, Double.class)
				.key(annotation.value())
				.build();
	}
	
	private static PropertyItem parseItem(Property<Locale> property, LocalePref annotation) {
		return buildItem(property, Locale.class)
				.key(annotation.value())
				.choices(localeManager.createLocaleList())
				.propertyType(PropertyType.SEARCHABLE_CHOICE)
				.build();
	}
	
	private static PropertyItem parseItem(Property<Integer> property, ColorPref annotation) {
		return buildItem(property, Integer.class)
				.key(annotation.value())
				.propertyType(PropertyType.COLOR)
				.build();
	}
	
	private static PropertyItem parseItem(Property<String> property, DirectoryPref annotation) {
		return buildItem(property, String.class)
				.key(annotation.value())
				.propertyType(PropertyType.DIRECTORY)
				.build();
	}
	
	
}
