package it.richkmeli.jframework.orm.dataexample.device;

import it.richkmeli.jframework.orm.DatabaseException;
import it.richkmeli.jframework.orm.DatabaseManager;
import it.richkmeli.jframework.orm.dataexample.device.model.Device;
import it.richkmeli.jframework.orm.dataexample.device.model.DeviceModel;

import java.util.ArrayList;
import java.util.List;

public class DeviceDatabaseManager extends DatabaseManager implements DeviceModel {

    public DeviceDatabaseManager(String database) throws DatabaseException {
        schemaName = "AuthSchema";
        tableName = schemaName + "." + "device";
        table = "(" +
                "name VARCHAR(50) NOT NULL," +
                "ip VARCHAR(25) NOT NULL," +
                "serverPort VARCHAR(10)," +
                "lastConnection VARCHAR(25)," +
                "encryptionKey VARCHAR(32)," +
                "userAssociated VARCHAR(50) REFERENCES auth(email)," +
                "commands TEXT," +
                "commandsOutput TEXT," +
                "PRIMARY KEY (name)" +
                ");";

        init(database);

    }


    public List<Device> refreshDevice() throws DatabaseException {
        List<Device> deviceList = new ArrayList<Device>();
        deviceList = refreshDevice("");
        return deviceList;
    }


    // TODO ORM aggiungi foreign keys
    public List<Device> refreshDevice(String user) throws DatabaseException {
        List<Device> devices = readAll(Device.class);
        if (devices != null) {
            // filter user devices
            List<Device> userDevices = new ArrayList<>();
            for (Device device : devices) {
                if (device.getUserAssociated().equalsIgnoreCase(user)) {
                    userDevices.add(device);
                }
            }
            return userDevices;
        } else {
            return null;
        }
    }

    /*public boolean addDevice(Device device) throws DatabaseException {
        return add(device);
    }*/

    public boolean addDevice(Device device) throws DatabaseException {
        return create(device);
    }

    public boolean editDevice(Device device) throws DatabaseException {
        return update(device);
    }

    public Device getDevice(String name) throws DatabaseException {
        return read(new Device(name, null, null, null, null, null, null, null));
    }


    public boolean removeDevice(String name) throws DatabaseException {
        return delete(new Device(name, null, null, null, null, null, null, null));
    }

    public String getEncryptionKey(String name) throws DatabaseException {
        Device device = getDevice(name);
        if (device != null) {
            return device.getEncryptionKey();
        } else {
            return null;
        }
    }

    public boolean editCommands(String deviceName, String commands) throws DatabaseException {
        return update(new Device(deviceName, null, null, null, null, null, commands, null));
    }

    public String getCommands(String deviceName) throws DatabaseException {
        Device device = getDevice(deviceName);
        if (device != null) {
            return device.getCommands();
        } else {
            return null;
        }
    }

    public boolean setCommandsOutput(String deviceName, String commandsOutput) throws DatabaseException {
        return update(new Device(deviceName, null, null, null, null, null, null, commandsOutput));
    }

    public String getCommandsOutput(String deviceName) throws DatabaseException {
        Device device = getDevice(deviceName);
        if (device != null) {
            return device.getCommandsOutput();
        } else {
            return null;
        }
    }

}