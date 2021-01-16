package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController // эта аннотация будет отсканированна с помощью @ComponentScan("com.space.service") класса AppConfig
// наследуется от аннотации @Component (тот же Component, но с рассширенными возможностями) когда "ComponentScan видит аннотацию @Controller она создает Bean  из этого помеченного java - класса
@RequestMapping("/rest")
//для методов эта аннотация считается устаревшей, однако может использоваться на самом классе контроллере
//это означает что мы хотим чтобы все методы, в даннном случае в контроллере ShipController,  в начале в адресе имели префикс /rest
// значит например к следующему методу getShipsList мы сможем обратиться по адресу /rest/ships
public class ShipsController {

    private ShipService service;

    public ShipsController() {
    }

    @Autowired
    // эта аннотация позволяет не внедрять зависимости вручную, а позволяет Spring'у делегировать эту задачу
    // в данном случае мы создаем конструктор для класса ShipController, в конструкторе указываем что ожидаем получить на вход какой то Bean реализующий интерфейс ShipService,
    // Spring сканирует все классы с аннотацией @Component  и создает Bean'ы для этих классов,
    // Spring сканирует все созданные бины и проверяет, подходит ли хотябы один бин в качестве зависимости там где мы указали аннотацию @Autowired,
    // если не находит ни одного бина  - ошибка, если несколько бинов подходят - неоднозначность
    // аннотация @Autowired подбирает подходящие бины по их типу(класс или интерфейс)
    // @Autowired можно использовать на полях, сеттерах, конструкторах, внедряет зависимость даже в приватное поле( делает Spring это с помощью рефлексии(Java Reflection API))
    public ShipsController(ShipService service) {
        this.service = service;
    }

    @GetMapping("/ships")
    // внутри контроллера находятся методы, обычно, но не всегда один метод соответствует одному URL'у (адресу по которому мы можем перейти в Web браузере
    //то есть при переходе в браузере на адрес /ships мы попадем в метод getShipsList
    // адрес /ships задается с помощью Mapping, Маппинги связывают метод контроллера с адресом, по которому можно к этому методу обратиться(из браузера например)
    //всего 5 разных видов маппинга - в зависимости от того, какой HTTP запрос(с каким HTTP методом) должен прийти в этот метод контроллера
    //@GetMapping @PostMapping @PutMapping @DeleteMapping @PatchMapping
    // иногда пишут (устаревший вариант) : @RequestMapping(method = RequestMethod.Get)
    public List<Ship> getShipsList(@RequestParam(value = "name", required = false) String name, // аннотация @RequestParam предназначена чтобы извлекать только параметры из поступающего GET запроса,
                                   // передаем ключ "name" значение которого Spring положит в String name
                                   // @RequestParam ждет что мы в URL передадим параметр (/ships?name=Orvill) который она внедрит в переменную, и если параметров в URL нет то выдаст ошибку,
                                   // если нехотим такого поведения то должны использовать required = false,
                                   // это означает что если мы передаем параметры то они внедряются в переменную, если не передаем то в переменные будет лежать null
                                   @RequestParam(value = "planet", required = false) String planet,
                                   @RequestParam(value = "shipType", required = false) ShipType shipType,
                                   @RequestParam(value = "after", required = false) Long after,
                                   @RequestParam(value = "before", required = false) Long before,
                                   @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                   @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                   @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                   @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                   @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                   @RequestParam(value = "minRating", required = false) Double minRating,
                                   @RequestParam(value = "maxRating", required = false) Double maxRating,
                                   @RequestParam(value = "order", required = false) ShipOrder order,
                                   @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                   @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {

        return service.getShipsList(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, order, pageNumber, pageSize);
    }

    @GetMapping("/ships/count")
    public Integer getShipsCount(@RequestParam(value = "name", required = false) String name,
                                 @RequestParam(value = "planet", required = false) String planet,
                                 @RequestParam(value = "shipType", required = false) ShipType shipType,
                                 @RequestParam(value = "after", required = false) Long after,
                                 @RequestParam(value = "before", required = false) Long before,
                                 @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                 @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                 @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                 @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                 @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                 @RequestParam(value = "minRating", required = false) Double minRating,
                                 @RequestParam(value = "maxRating", required = false) Double maxRating) {

        return service.getShipsCount(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
    }

    @GetMapping("/ships/{id}")
    public ResponseEntity<Ship> getShip(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //400
        }
        Ship ship = service.getShip(id);
        if (ship == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); //404
        }
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @DeleteMapping("/ships/{id}")
    public ResponseEntity<Ship> deleteShip(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (service.getShip(id) != null) {
            service.deleteShip(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/ships")
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {
        if (ship != null
                && ship.getName() != null && isStringValid(ship.getName())
                && ship.getPlanet() != null && isStringValid(ship.getPlanet())
                && ship.getShipType() != null
                && ship.getProdDate() != null && isDateValid(ship.getProdDate())
                && ship.getSpeed() != null && isSpeedValid(ship.getSpeed())
                && ship.getCrewSize() != null && isCrewSizeValid(ship.getCrewSize())) {

            if (ship.getUsed() == null) ship.setUsed(false);
            return new ResponseEntity<>(service.createShip(ship), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/ships/{id}")
    public ResponseEntity<Ship> updateShip(@PathVariable("id") Long id, @RequestBody Ship ship) {
        //Для работы с параметрами, передаваемыми через адрес запроса в Spring WebMVC используется аннотаци
        if (id == null || id <= 0 || ship == null
                || ship.getName() != null && !isStringValid(ship.getName())
                || ship.getPlanet() != null && !isStringValid(ship.getPlanet())
                || ship.getProdDate() != null && !isDateValid(ship.getProdDate())
                || ship.getSpeed() != null && !isSpeedValid(ship.getSpeed())
                || ship.getCrewSize() != null && !isCrewSizeValid(ship.getCrewSize())) {

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Ship updateShip = service.updateShip(id, ship);

        return updateShip == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(updateShip, HttpStatus.OK);
    }

    private boolean isStringValid(String param) {
        return !param.isEmpty() && param.length() <= 50;
    }

    private boolean isSpeedValid(Double speed) {
        double result = new BigDecimal(speed).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return result >= 0.01 && result <= 0.99;

    }

    private boolean isCrewSizeValid(Integer size) {
        return size > 0 && size < 10_000;
    }

    private boolean isDateValid(Date date) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.YEAR, 2800);
        Date from = calendar1.getTime();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.YEAR, 3019);
        Date to = calendar2.getTime();

        return date.getTime() > 0 && date.after(from) && date.before(to);
    }


}