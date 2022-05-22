package vn.edu.fpt.capstone.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.edu.fpt.capstone.common.Message;
import vn.edu.fpt.capstone.dto.AddressDto;
import vn.edu.fpt.capstone.dto.ResponseObject;
import vn.edu.fpt.capstone.service.AddressService;
import vn.edu.fpt.capstone.service.PhuongXaService;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AddressController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AddressController.class.getName());

	@Autowired
	AddressService addressService;
	@Autowired
	PhuongXaService phuongXaService;

	@GetMapping(value = "/address/{id}")
	public ResponseEntity<ResponseObject> getById(@PathVariable String id) {
		ResponseObject responseObject = new ResponseObject();
		try {
			Long lId = Long.valueOf(id);
			if (addressService.isExist(lId)) {
				AddressDto addressDto = addressService.findById(lId);
				responseObject.setResults(addressDto);
				responseObject.setCode("200");
				responseObject.setMessage(Message.OK);
				return new ResponseEntity<>(responseObject, HttpStatus.OK);
			} else {
				responseObject.setCode("404");
				responseObject.setMessage(Message.NOT_FOUND);
				return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
			}
		} catch (NumberFormatException e) {
			LOGGER.error(e.toString());
			responseObject.setCode("404");
			responseObject.setMessage(Message.NOT_FOUND);
			return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
		} catch (Exception ex) {
			LOGGER.error(ex.toString());
			responseObject.setCode("500");
			responseObject.setMessage(Message.INTERNAL_SERVER_ERROR);
			return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/address")
	public ResponseEntity<ResponseObject> getAll() {
		ResponseObject responseObject = new ResponseObject();
		try {
			List<AddressDto> addressDtos = addressService.findAll();
			if (addressDtos == null || addressDtos.isEmpty()) {
				responseObject.setCode("404");
				responseObject.setMessage(Message.NOT_FOUND);
				return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
			}
			responseObject.setResults(addressDtos);
			responseObject.setCode("200");
			responseObject.setMessage(Message.OK);
			return new ResponseEntity<>(responseObject, HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error(ex.toString());
			responseObject.setCode("500");
			responseObject.setMessage(Message.INTERNAL_SERVER_ERROR);
			return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/address")
	public ResponseEntity<ResponseObject> postAddress(@RequestBody AddressDto addressDto) {
		ResponseObject response = new ResponseObject();
		try {
			if (addressDto.getId() != null
					|| (addressDto.getXaId() == null || !phuongXaService.isExist(addressDto.getXaId()))) {
				response.setCode("406");
				response.setMessage(Message.NOT_ACCEPTABLE);
				return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
			}

			AddressDto addressDto2 = addressService.createAddress(addressDto);
			if (addressDto2 == null) {
				response.setCode("500");
				response.setMessage(Message.INTERNAL_SERVER_ERROR);
				return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			response.setCode("200");
			response.setMessage(Message.OK);
			response.setResults(addressDto2);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.toString());
			response.setCode("500");
			response.setMessage(Message.INTERNAL_SERVER_ERROR);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping(value = "/address")
	public ResponseEntity<ResponseObject> putAddress(@RequestBody AddressDto addressDto) {
		ResponseObject response = new ResponseObject();
		try {
			if (addressDto.getId() == null || !addressService.isExist(addressDto.getId())) {
				response.setCode("404");
				response.setMessage(Message.NOT_FOUND);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}
			if (addressDto.getXaId() == null || !phuongXaService.isExist(addressDto.getXaId())) {
				response.setCode("406");
				response.setMessage(Message.NOT_ACCEPTABLE);
				return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
			}
			AddressDto addressDto2 = addressService.updateAddress(addressDto);
			response.setCode("200");
			response.setMessage(Message.OK);
			response.setResults(addressDto2);

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.toString());
			response.setCode("500");
			response.setMessage(Message.INTERNAL_SERVER_ERROR);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping(value = "/address/{id}")
	public ResponseEntity<ResponseObject> deleteAddress(@PathVariable String id) {
		ResponseObject response = new ResponseObject();
		try {
			if (id == null || id.isEmpty() || !addressService.isExist(Long.valueOf(id))) {
				response.setCode("404");
				response.setMessage(Message.NOT_FOUND);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}
			response.setCode("200");
			response.setMessage(Message.OK);
			addressService.removeAddress(Long.valueOf(id));
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (NumberFormatException ex) {
			response.setCode("404");
			response.setMessage(Message.NOT_FOUND);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			LOGGER.error(e.toString());
			response.setCode("500");
			response.setMessage(Message.INTERNAL_SERVER_ERROR);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
