package gov.ca.cwds.idm.persistence.cwscms.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue(value = "GVR_ENTC")
public class County extends SystemCodeTable {
}
