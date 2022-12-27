package ca.skylinedata.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class NotesListView {
	@Valid
	private List<Note> noteList = new ArrayList<Note>();
}
