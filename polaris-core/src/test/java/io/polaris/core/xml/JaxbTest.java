package io.polaris.core.xml;

import io.polaris.core.TestConsole;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.bind.annotation.*;

public class JaxbTest {
	@Test
	void test01() {
		SchoolVo schoolVo = new SchoolVo();
		schoolVo.setSchoolName("101学校");
		schoolVo.setSchoolAddress("101");

		RoomVo roomVo = new RoomVo();
		roomVo.setRoomName("101教室");
		roomVo.setRoomNo("101");
		schoolVo.setRoom(roomVo);

		TestConsole.println(Jaxb.toXml(schoolVo));
		SchoolVo bean = Jaxb.toBean(Jaxb.toXml(schoolVo), SchoolVo.class);
		TestConsole.println(bean);
		Assertions.assertEquals(bean.getSchoolName(), schoolVo.getSchoolName());
		Assertions.assertEquals(bean.getRoom().getRoomName(), schoolVo.getRoom().getRoomName());
	}

	@XmlRootElement(name = "school")
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(propOrder = {"schoolName", "schoolAddress", "room"})
	@ToString
	@EqualsAndHashCode
	public static class SchoolVo {
		@XmlElement(name = "school_name", required = true)
		private String schoolName;
		@XmlElement(name = "school_address", required = true)
		private String schoolAddress;
		@XmlElement(name = "room", required = true)
		private RoomVo room;

		@XmlTransient
		public String getSchoolName() {
			return schoolName;
		}

		public void setSchoolName(String schoolName) {
			this.schoolName = schoolName;
		}

		@XmlTransient
		public String getSchoolAddress() {
			return schoolAddress;
		}

		public void setSchoolAddress(String schoolAddress) {
			this.schoolAddress = schoolAddress;
		}

		@XmlTransient
		public RoomVo getRoom() {
			return room;
		}

		public void setRoom(RoomVo room) {
			this.room = room;
		}

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(propOrder = {"roomNo", "roomName"})
	@ToString
	@EqualsAndHashCode
	public static final class RoomVo {
		@XmlElement(name = "room_no", required = true)
		private String roomNo;
		@XmlElement(name = "room_name", required = true)
		private String roomName;

		@XmlTransient
		public String getRoomNo() {
			return roomNo;
		}

		public void setRoomNo(String roomNo) {
			this.roomNo = roomNo;
		}

		@XmlTransient
		public String getRoomName() {
			return roomName;
		}

		public void setRoomName(String roomName) {
			this.roomName = roomName;
		}
	}
}
