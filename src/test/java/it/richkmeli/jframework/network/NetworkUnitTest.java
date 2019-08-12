package it.richkmeli.jframework.network;

import org.junit.Test;

public class NetworkUnitTest {


    @Test
    public void requestAsync() {
        final Lock lock = new Lock();

        RequestListener<MeasurementsResponse> requestListener = new RequestListener<MeasurementsResponse>() {
            @Override
            public void onResult(MeasurementsResponse response) {
                synchronized (lock) {
                    lock.notify();
                }

                System.out.println("TEST_1: " + response.getLocation());
            }
        };

        RequestAsync requestAsync = new RequestAsync(requestListener, MeasurementsResponse.class, "https://api.openaq.org/v1/measurements?limit=1");
        requestAsync.start();

        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

// extends Object
class Lock {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

class MeasurementsResponse {
    private Date date;
    private String parameter;
    private double value;
    private String unit;
    private String location;
    private String country;
    private String city;
    private Coordinates coordinates;
    private String sourceName;

    public MeasurementsResponse(Date date, String parameter, double value, String unit, String location, String country, String city, Coordinates coordinates, String sourceName) {
        this.date = date;
        this.parameter = parameter;
        this.value = value;
        this.unit = unit;
        this.location = location;
        this.country = country;
        this.city = city;
        this.coordinates = coordinates;
        this.sourceName = sourceName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}

class Date {
    String utc;
    String local;
}

class Coordinates {
    double latitude;
    double longitude;
}