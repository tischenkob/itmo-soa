package ru.ifmo.worker.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "country")
public enum Country {
	RUSSIA,
	USA,
	ITALY,
	NORTH_KOREA
}