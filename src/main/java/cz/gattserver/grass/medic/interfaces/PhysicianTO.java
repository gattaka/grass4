package cz.gattserver.grass.medic.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import cz.gattserver.common.Identifiable;

public class PhysicianTO implements Identifiable<Long> {

	private Long id;

	/**
	 * Jméno
	 */
	@NotNull
	@Size(min = 1)
	private String name = "";

    /**
     * Email
     */
    private String email;

    /**
     * Telefon
     */
    private String phone;

    public PhysicianTO() {
    }

    @QueryProjection
    public PhysicianTO(Long id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    @Override
	public Long getId() {
		return id;
	}

    @Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PhysicianTO) {
			PhysicianTO dto = (PhysicianTO) obj;
			if (dto.getId() == null)
				return id == null;
			else
				return dto.getId().equals(id);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

}