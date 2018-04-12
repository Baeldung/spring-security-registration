package com.baeldung.arraysinthymleaf.contollers;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ArrayController
{
	@GetMapping("/")
	public String arrayController(Model model)
	{
		List<String> countinentsList = new ArrayList<String>();

		countinentsList.add("Africa");
		countinentsList.add("Antarctica");
		countinentsList.add("Assia");
		countinentsList.add("Australia");
		countinentsList.add("Europe");
		countinentsList.add("North America");
		countinentsList.add("Sourth America");

		model.addAttribute("countinents", countinentsList);

		return "continents";
	}
}
