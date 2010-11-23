package engine;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import util.FloatOption;
import util.IntOption;

public class Options {

	public boolean SEQUENTIAL_LEARNING = true;
	public int LEARN_TIME = 10 * 10 * 30;

	@IntOption(min = 256, max = 1024)
	public int SENSORS = 512;

	@IntOption(min = 1, max = 4)
	public int LAYERS = 1;

	@IntOption(min = 1, max = 8)
	public int NEURON_CELLS = 3;

	@IntOption(min = 1, max = 40)
	public int NEURON_NEW_SYNAPSES = 10;

	@IntOption(min = 1, max = 20)
	public int NEURON_ACTIVATION_THRESHOLD = 5;

	@IntOption(min = 1, max = 20)
	public int NEURON_MIN_THRESHOLD = 2;

	@IntOption(min = 1, max = 100)
	public int NEURON_PERMANENCE_INC = 10;

	@IntOption(min = 1, max = 100)
	public int NEURON_PERMANENCE_DEC = 5;

	@FloatOption(min = 0, max = 10)
	public double SENSOR_BOOST = 0.3;

	@IntOption(min = 1, max = 40)
	public int SENSOR_WINNERS = 8;

	@IntOption(min = 1, max = 255 * 32 * 32)
	public int SENSOR_MIN_OVERLAP = 500;

	@IntOption(min = 1, max = 100)
	public int PERMANENCE_CONNECTED = 10;

	@IntOption(min = 1, max = 100)
	public int PERMANENCE_INITIAL = 30;

	@IntOption(min = 1, max = 100)
	public int SENSOR_PERMANENCE_INC = 10;

	@IntOption(min = 1, max = 100)
	public int SENSOR_PERMANENCE_DEC = 5;

	@IntOption(min = 1, max = 100)
	public int SENSOR_PERMANENCE_CONNECTED = 10;

	@IntOption(min = 1, max = 100)
	public int SENSOR_PERMANENCE_INITIAL = 30;

	static Map<Class<?>, String> availableTypes = new HashMap<Class<?>, String>();
	static {
		availableTypes.put(int.class, "int");
		availableTypes.put(double.class, "float");
		availableTypes.put(boolean.class, "boolean");
	}

	private static String fieldInfo(Field field) {
		String type = availableTypes.get(field.getType());
		if(type == null){
			throw new IllegalArgumentException("Unsupported field "+field.getName()+" of type "+field.getType());
		}
		for (Annotation ann : field.getAnnotations()) {
			if (ann instanceof IntOption) {
				IntOption opt = (IntOption) ann;
				if(!type.equals("int")){
					throw new IllegalArgumentException("Can't use IntField on "+field.getName()+" of type "+field.getType());
				}
				type = "int (" + opt.min() + " .. " + opt.max()+")";
			} else if (ann instanceof FloatOption) {
				FloatOption opt = (FloatOption) ann;
				if(!type.equals("float")){
					throw new IllegalArgumentException("Can't use FloatField on "+field.getName()+" of type "+field.getType());
				}
				type = "float (" + opt.min() + " .. " + opt.max()+")";
			}
		}
		return type;
	}

	public static String[] fields() {
		ArrayList<String> output = new ArrayList<String>();
		for (Field field : Options.class.getDeclaredFields()) {
			String name = field.getName();
			if (isValidName(name)) {
				output.add(name + ": " + fieldInfo(field));
			}
		}
		return output.toArray(new String[0]);
	}

	private static boolean isValidName(String name) {
		return name.equals(name.toUpperCase());
	}

	public void setOption(String key, String value) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		this.getClass().getField(key).set(this, value);
	}

	@Override
	public String toString() {
		return configuration();
	}

	private String configuration() {
		StringBuilder sb = new StringBuilder();
		for(Field field: this.getClass().getDeclaredFields()){
			String name = field.getName();
			if (isValidName(name)){
				String type = fieldInfo(field);
				Object value = "(error)";
				try {
					value = field.get(this);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				sb.append(name).append(": ");
				sb.append(type).append(" = ");
				sb.append(value);
				sb.append("\n");
			}
		}
		return sb.toString();
	}
}
