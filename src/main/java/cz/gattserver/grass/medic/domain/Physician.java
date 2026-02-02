package cz.gattserver.grass.medic.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "PHYSICIAN")
public class Physician {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Jméno
	 */
	private String name;

    /**
     * Email
     */
    private String email;

    /**
     * Telefon
     */
    private String phone;

	public Long getId() {
		return id;
	}

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
}