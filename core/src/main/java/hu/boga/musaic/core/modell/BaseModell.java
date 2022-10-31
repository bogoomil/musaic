package hu.boga.musaic.core.modell;

import com.google.common.base.Objects;

import java.util.UUID;

public class BaseModell {
    private String id;

    public BaseModell(String id) {
        this.id = id;
    }

    public BaseModell(){
        this(UUID.randomUUID().toString());
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseModell that = (BaseModell) o;
        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
