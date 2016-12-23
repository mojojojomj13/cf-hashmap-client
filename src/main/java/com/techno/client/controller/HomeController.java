package com.techno.client.controller;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.techno.client.exception.ServiceException;

/**
 * This is the home controller
 * 
 * @author Prithvish Mukherjee
 *
 */
@Controller
public class HomeController {

	public static final String SERVICE_INSTANCES = "https://cf-hashmap-broker.cfapps.io/v2/service_instances/";
	public static final String MODEL_ALL_SERVICES = "allServices";
	public static final String MODEL_A_SERVICE = "aService";
	public static final String HOME_VIEW = "home";
	public static final String ALL_SERVICES = "/getAllServices";
	public static final String CATALOG = "https://cf-hashmap-broker.cfapps.io/v2/catalog";

	@RequestMapping(value = "/")
	public ModelAndView home(RedirectAttributes flash) {
		return new ModelAndView(HOME_VIEW);
	}

	@RequestMapping(value = ALL_SERVICES)
	public ModelAndView getAllServices(RedirectAttributes flash) {
		String msg = "";
		ModelAndView mv = new ModelAndView();
		mv.setViewName("redirect:/");
		try {
			JsonObject jsonObj = callOutbound(CATALOG, HttpMethod.GET, createRequestEntity());
			ArrayList<String> sId = new ArrayList<String>();
			if (jsonObj.has("services")) {
				JsonArray arr = jsonObj.get("services").getAsJsonArray();
				if (arr.size() > 0)
					for (JsonElement jsonElement : arr) {
						sId.add(((JsonObject) jsonElement).get("id").getAsString());
					}
			}
			flash.addFlashAttribute("sId", sId);
			msg = jsonObj.toString();
		} catch (ServiceException e) {
			msg = e.toString();
		}
		// mv.addObject(MODEL_ALL_SERVICES, msg);
		flash.addFlashAttribute(MODEL_ALL_SERVICES, msg);
		return mv;
	}

	@RequestMapping(value = "/createServiceInstance")
	public ModelAndView createServiceInstance(RedirectAttributes flash) {
		String msg = "";
		ModelAndView mv = new ModelAndView();
		mv.setViewName(HOME_VIEW);
		try {
			JsonObject jsonObj = callOutbound(SERVICE_INSTANCES + UUID.randomUUID().toString(), HttpMethod.PUT,
					createRequestEntity());
			mv = getAllServices(flash);
			msg = (String) mv.getModel().get(MODEL_ALL_SERVICES);
		} catch (ServiceException e) {
			msg = e.toString();
		}
		mv.addObject(MODEL_ALL_SERVICES, msg);
		return mv;
	}

	@RequestMapping(value = "/getServiceInfo")
	public ModelAndView createServiceInstance(RedirectAttributes flash,
			@RequestParam(value = "selectedSid", required = true) String serviceId,
			@RequestParam(value = "action", required = true) String actionName) {
		String msg = "";
		JsonObject jsonObj = new JsonObject();
		ModelAndView mv = new ModelAndView();
		mv.setViewName(HOME_VIEW);
		try {
			if (actionName.equals("GET SERVICE INFO"))
				jsonObj = callOutbound(SERVICE_INSTANCES + serviceId, HttpMethod.GET, createRequestEntity());
			if (actionName.equals("BIND A SERVICE"))
				jsonObj = callOutbound(
						SERVICE_INSTANCES + serviceId + "/service_bindings/" + UUID.randomUUID().toString(),
						HttpMethod.PUT, createRequestEntity());
			mv = getAllServices(flash);
			flash.addFlashAttribute(MODEL_A_SERVICE, jsonObj.toString());
			msg = (String) mv.getModel().get(MODEL_ALL_SERVICES);
		} catch (ServiceException e) {
			msg = e.toString();
		}
		mv.addObject(MODEL_A_SERVICE, msg);
		return mv;
	}

	private JsonObject callOutbound(String url, HttpMethod method, HttpEntity<String> entity) throws ServiceException {
		JsonObject jsonRes = new JsonObject();
		if (null != url && !url.isEmpty()) {
			try {
				RestTemplate template = new RestTemplate();
				ResponseEntity<String> respEntity = template.exchange(url, method, entity, String.class);
				jsonRes = getResponseObject(respEntity.getBody());
			} catch (JsonSyntaxException | RestClientException e) {
				throw new ServiceException("Some error while calling calling the service :: " + e.toString(), e,
						HttpStatus.SERVICE_UNAVAILABLE);
			}
		}
		return jsonRes;
	}

	private static JsonObject getResponseObject(String jsonString) throws JsonSyntaxException {
		Gson gson = new Gson();
		JsonObject jsonRes = new JsonObject();
		jsonRes = gson.fromJson(jsonString, JsonObject.class);
		return jsonRes;
	}

	private HttpEntity<String> createRequestEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		List<MediaType> accept = new ArrayList<MediaType>();
		accept.add(MediaType.APPLICATION_JSON);
		headers.setAccept(accept);
		HttpEntity<String> reqEntity = new HttpEntity<String>("", headers);
		return reqEntity;
	}
}
