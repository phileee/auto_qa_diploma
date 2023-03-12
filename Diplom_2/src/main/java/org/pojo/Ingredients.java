package org.pojo;

import java.util.List;

public class Ingredients {
    private boolean success;
    private List<Ingredient> data;

    public Ingredients(boolean success, List<Ingredient> data) {
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Ingredient> getData() {
        return data;
    }

    public void setData(List<Ingredient> data) {
        this.data = data;
    }


}
