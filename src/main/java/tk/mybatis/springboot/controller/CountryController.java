/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package tk.mybatis.springboot.controller;

import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tk.mybatis.springboot.model.Country;
import tk.mybatis.springboot.service.CountryService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/countries")
public class CountryController {
    private Logger logger = LoggerFactory.getLogger(CountryController.class);

    @Autowired
    private CountryService countryService;

    @Autowired
    private HttpSession autowiredSession;
    @GetMapping
    public ModelAndView testSession(HttpServletRequest request, HttpSession httpSession) {
        HttpSession requestSession = request.getSession();
        logger.info("httpSession id : {}", httpSession.getId());
        logger.info("requestSession id : {}", requestSession.getId());
        logger.info("autowiredSession id : {}", autowiredSession.getId());
        requestSession.invalidate();
        logger.info("requestSession id : {}", requestSession.getId());
        requestSession = request.getSession();
        logger.info("httpSession id : {}", httpSession.getId());
        logger.info("requestSession id : {}", requestSession.getId());
        logger.info("autowiredSession id : {}", autowiredSession.getId());
        return null;
    }

    public ModelAndView getAll(Country country, HttpServletRequest request, HttpSession httpSession) {
        ModelAndView result = new ModelAndView("index");
        List<Country> countryList = countryService.getAll(country);
        result.addObject("pageInfo", new PageInfo<Country>(countryList));
        result.addObject("queryParam", country);
        result.addObject("page", country.getPage());
        result.addObject("rows", country.getRows());
        HttpSession requestSession = request.getSession();
        logger.info("httpSession id : {}", httpSession.getId());
        logger.info("requestSession id : {}", requestSession.getId());
        logger.info("autowiredSession id : {}", autowiredSession.getId());
//        requestSession.setAttribute("test1", "test1");
//        requestSession.setAttribute("test2", "test2");
//        autowiredSession.setAttribute("test3", "test3");
//        httpSession.setAttribute("a", "a");
//        logger.info("httpSession test1 : {}", httpSession.getAttribute("test1"));
//        logger.info("httpSession test2 : {}", httpSession.getAttribute("test2"));

        requestSession.invalidate();
//        httpSession.invalidate();
        logger.info("requestSession id : {}", requestSession.getId());
        requestSession = request.getSession();
//        httpSession.setAttribute("test2", "test2");
//        HttpSession httpSession2 = request.getSession();

        logger.info("<------------------->");
        logger.info("httpSession id : {}", httpSession.getId());
        logger.info("requestSession id : {}", requestSession.getId());
        logger.info("autowiredSession id : {}", autowiredSession.getId());

        return result;
    }

    @RequestMapping(value = "/add")
    public ModelAndView add() {
        ModelAndView result = new ModelAndView("view");
        result.addObject("country", new Country());
        return result;
    }

    @RequestMapping(value = "/view/{id}")
    public ModelAndView view(@PathVariable Integer id) {
        ModelAndView result = new ModelAndView("view");
        Country country = countryService.getById(id);
        result.addObject("country", country);
        return result;
    }

    @RequestMapping(value = "/delete/{id}")
    public ModelAndView delete(@PathVariable Integer id, RedirectAttributes ra) {
        ModelAndView result = new ModelAndView("redirect:/countries");
        countryService.deleteById(id);
        ra.addFlashAttribute("msg", "删除成功!");
        return result;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ModelAndView save(Country country) {
        ModelAndView result = new ModelAndView("view");
        String msg = country.getId() == null ? "新增成功!" : "更新成功!";
        countryService.save(country);
        result.addObject("country", country);
        result.addObject("msg", msg);
        return result;
    }
}
